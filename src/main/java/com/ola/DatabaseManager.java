package com.ola;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.ola.model.*;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.thymeleaf.util.DateUtils;

import java.sql.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * Created on 11/29/15.
 */
public class DatabaseManager {

  private final static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
  private final static String JDBC_URL = "jdbc:mysql://localhost:3306/wwsi_prod";
  private final static String DB_USER = "wwsi_prod";
  private final static String DB_PASS = "wwsi2015!";

  private Connection connect = null;

  public DatabaseManager() throws SQLException {
//    connect = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
//
//    String databaseUrl = JDBC_URL;
    // create a connection source to our database
    ConnectionSource connectionSource =  new JdbcConnectionSource(JDBC_URL, DB_USER, DB_PASS);

    // instantiate the dao
    Dao<User, Long> userDao = DaoManager.createDao(connectionSource, User.class);

    User user = new User();
    user.setName("kicha_zdzicha");
    user.setPassword("kartofle");
    User dbUser = userDao.queryBuilder().where().eq(User.COLUMN_NAME, user.getName()).queryForFirst();
    if (dbUser == null) {
      userDao.create(user);
    }

    User user2 = new User();
    user2.setName("jankowalski");
    user2.setPassword("kiwi");
    userDao.createOrUpdate(user2);

    System.out.println("All users:");
    List<User> userList = userDao.queryForAll();
    for (User dbUser : userList) {
      System.out.println(dbUser);
    }

    System.out.println("Filtered users:");
    List<User> filteredUserList = userDao.queryBuilder().where().eq(User.COLUMN_NAME, "jankowalski").query();
    for (User dbUser : filteredUserList) {
      System.out.println(dbUser);
    }

  }

  public String register(User user) throws SQLException{
    if(checkUserExistInDbByName(user)){
      return null;
    }
    String makeNewUser = "INSERT INTO USER (NAME,PASSWORD) VALUES (\'" + user.getName() + "\', \'"  + user.getPassword() + "\');";
    PreparedStatement statement = connect.prepareStatement(makeNewUser);
    int result = statement.executeUpdate();
    if(result == 1){
      //check if the user is in database and generate session token
      return login(user); //returns sesion or null when sth goes wrong
    }else{
      //result == 2 or 0, no rows in db were manipulated
      return null;
    }
  }

  public String login(User user) throws SQLException{
    User userFromDb = getUserFromDbForNameAndPassword(user);
    if(userFromDb != null){
      //user is in db, password is correct -> make or session
      return  makeSession(userFromDb);
    }else{
      //invalid password
      //no user name in database
      return  null;
    }
  }

  public User getUserFromDbForNameAndPassword(User user) throws SQLException{
    String queryCheckUser = "SELECT * FROM USER WHERE NAME = \'" + user.getName() + "' AND password =\'" + user.getPassword()+"\';";
    PreparedStatement isUserInDatabase = connect.prepareStatement(queryCheckUser);
    ResultSet resultSet = isUserInDatabase.executeQuery();
    User userFromDb = User.fromResultSet(resultSet);
    return userFromDb;
  }

  public boolean checkUserExistInDbByName(User user) throws SQLException{
    String queryCheckUser = "SELECT * FROM USER WHERE NAME = \'" + user.getName() +"\';";
    PreparedStatement isUserInDatabase = connect.prepareStatement(queryCheckUser);
    ResultSet resultSet = isUserInDatabase.executeQuery();
    User userFromDb = User.fromResultSet(resultSet);
    if(userFromDb != null){
      return true;
    }else{
      return false;
    }
  }

  public String makeSession(User userFromDb) throws SQLException{
    //delete all expired sessions
    deleteExpiredSesions();
    long expPeriod = 3600000; //hour

    String queryCheckSesion= "SELECT * FROM SESION WHERE ID_USER = " + userFromDb.getId() + ";";
    PreparedStatement isUserInDatabase = connect.prepareStatement(queryCheckSesion);
    ResultSet resultSet = isUserInDatabase.executeQuery();
    if(!resultSet.next()){
      //no sesion for this user
      String sessionToken = makeNewSessionFroUser(userFromDb, expPeriod);
      return  sessionToken;
    }else {
      Long expDate = resultSet.getLong(resultSet.findColumn("EXP_DATE"));
      String token = resultSet.getString(resultSet.findColumn("TOKEN"));
      long currentTime = System.currentTimeMillis();
      //update egsisting sesion for this user
      long expDateToSave = (System.currentTimeMillis()+expPeriod);
      String query = "UPDATE SESION SET EXP_DATE = " + expDateToSave + " WHERE ID_USER = " + userFromDb.getId() + "AND TOKEN = " + token;
      PreparedStatement deleteSesion = connect.prepareStatement(query);
      return  token;
    }
  }

  public String makeNewSessionFroUser (User userFromDb, long expPeriod) throws SQLException{
    UUID token = UUID.randomUUID();
    //save session to database
    long expDateToSave = (System.currentTimeMillis()+expPeriod);
    String query = "INSERT INTO SESION (TOKEN, ID_USER, EXP_DATE) VALUES (\'" + token.toString() + "\'," + userFromDb.getId() + "," +
            expDateToSave + ");";
    PreparedStatement preparedStatement = connect.prepareStatement(query);
    int test = preparedStatement.executeUpdate();
    if(test == 1){
      return  token.toString();
    }else{
      return null; // TODO: Handle null
    }

  }

  public void deleteExpiredSesions() throws SQLException{
    String query = "DELETE FROM SESION WHERE (EXP_DATE < " + System.currentTimeMillis()+" AND ID_USER<>0);";
    PreparedStatement statement = connect.prepareStatement(query);
    statement.executeUpdate();
  }

}

