package com.ola;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.PreparedUpdate;
import com.j256.ormlite.support.ConnectionSource;
import com.ola.model.*;

import java.sql.*;
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
  private Dao<User, Long> userDao;
  private Dao<Sesion, Long> sesionDao;
  private Dao<FeedUser, Long> feedUserDao;
  private Dao<Feed, Long> feedDao;

  public DatabaseManager() throws SQLException {
//    connect = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
//
//    String databaseUrl = JDBC_URL;
    // create a connection source to our database
    ConnectionSource connectionSource =  new JdbcConnectionSource(JDBC_URL, DB_USER, DB_PASS);

    // instantiate the DAOs
    userDao = DaoManager.createDao(connectionSource, User.class);
    sesionDao = DaoManager.createDao(connectionSource, Sesion.class);
    feedUserDao = DaoManager.createDao(connectionSource, FeedUser.class);
    feedDao = DaoManager.createDao(connectionSource, Feed.class);
  }

  public String register(User user) throws SQLException{
    if(checkUserExistInDbByName(user)){
      return null;
    }
    int rowsAffected =  userDao.create(user);
    if(rowsAffected == 1){
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

  // HELPER METHODS
  public boolean checkUserExistInDbByName(User user) throws SQLException{
    //TODO czemu to zapisuje a nie sprawdze
    User dbUser = userDao.queryBuilder().where().eq(User.COLUMN_NAME, user.getName()).queryForFirst();
    if (dbUser == null) {
      return false;
    }else{
      return true;
    }
  }

  public User getUserFromDbForNameAndPassword(User user) throws SQLException{
    //TODO: to zapytanie nie działa, zawsze zwraca null
    User filtereduser = userDao.queryBuilder().where().eq(User.COLUMN_NAME, user.getName()).and().eq(User.COLUMN_PASSWORD, user.getPassword()).queryForFirst();
    if(filtereduser != null){
      return filtereduser;
    }else {
      return null;
    }
  }

  public String makeSession(User userFromDb) throws SQLException{
    //delete all expired sessions
    deleteExpiredSesions();
    long expPeriod = 3600000; //hour

    Sesion sesionUpdate = sesionDao.queryBuilder().where().eq(Sesion.COLUMN_ID_USER, userFromDb.getId()).queryForFirst();
    if(sesionUpdate == null){
      //no sesion for this user
      String sessionToken = makeNewSessionFroUser(userFromDb, expPeriod);
      return  sessionToken;
    }else {
      sesionUpdate.setExpDate(System.currentTimeMillis() + expPeriod);
      sesionDao.update(sesionUpdate);
      return  sesionUpdate.getToken();
    }
  }

  public String makeNewSessionFroUser (User userFromDb, long expPeriod) throws SQLException{
    UUID token = UUID.randomUUID();
    long expDateToSave = (System.currentTimeMillis()+expPeriod);
    Sesion sesionToSave = new Sesion();
    sesionToSave.setToken(token.toString());
    sesionToSave.setExpDate(expDateToSave);
    sesionToSave.setIdUser(userFromDb.getId());
    //save session to database
    int result = sesionDao.create(sesionToSave);
    if(result == 1){
      return  sesionToSave.getToken();
    }else{
      return null; // TODO: Handle null
    }
  }

  public void deleteExpiredSesions() throws SQLException{
    sesionDao.deleteBuilder().where().le(Sesion.COLUMN_EXP_DATE, System.currentTimeMillis());
    sesionDao.deleteBuilder().delete();
  }

  public boolean saveFeeds(FeedRequest feedRequest) throws SQLException{
    long userId =  getUserIdWithToken(feedRequest.getToken());
    if(userId == 0){
      return false; //user is not logged - it shouldn happen - handled on mobile
    }else{
      long syncTime = System.currentTimeMillis();

      //save CREATED AND UPDATED with user id
      for(String url : feedRequest.getCreatedUpdated()){
        //FEED
        long feedId;
        Feed feedFromDb = feedDao.queryBuilder().where().eq(Feed.COLUMN_URL, url).queryForFirst();
        if(feedFromDb != null){
          feedFromDb.setSyncTimestamp(syncTime);
          feedDao.update(feedFromDb);
          feedId = feedFromDb.getId();
        }else{
          Feed feedToAdd = new Feed();
          feedToAdd.setUrl(url);
          feedToAdd.setSyncTimestamp(syncTime);
          feedDao.create(feedToAdd);
          feedId = feedToAdd.getId();
        }
        //FEED_USER
        FeedUser feedUser = feedUserDao.queryBuilder().where().eq(FeedUser.COLUMN_ID_FEED, feedId).and().eq(FeedUser.COLUMN_ID_USER, userId).queryForFirst();
        if(feedUser != null){
          //update feed_user entitty
          feedUser.setUpdateDate(syncTime);
          feedUser.setDeleted(false);
          feedUserDao.update(feedUser);
        }else{
          //create feed_user entitty for given id_user and id_feed
          FeedUser feedUserToSave = new FeedUser();
          feedUserToSave.setIdFeed(feedId);
          feedUserToSave.setIdUser(userId);
          feedUserToSave.setUpdateDate(syncTime);
          feedUserToSave.setDeleted(false);
          feedUserDao.create(feedUserToSave);
        }
      }

      //save DELTED with user id
      for(String url : feedRequest.getDeleted()){
        //FEED
        long feedId;
        Feed feedFromDb = feedDao.queryBuilder().where().eq(Feed.COLUMN_URL, url).queryForFirst();
        if(feedFromDb != null){
          feedFromDb.setSyncTimestamp(syncTime);
          feedDao.update(feedFromDb);
          feedId = feedFromDb.getId();
        }else{
          Feed feedToAdd = new Feed();
          feedToAdd.setUrl(url);
          feedToAdd.setSyncTimestamp(syncTime);
          feedDao.create(feedToAdd);
          feedId = feedToAdd.getId();
        }
        //FEED_USER
        FeedUser feedUser = feedUserDao.queryBuilder().where().eq(FeedUser.COLUMN_ID_FEED, feedId).and().eq(FeedUser.COLUMN_ID_USER, userId).queryForFirst();
        if(feedUser != null){
          //update feed_user entitty
          feedUser.setUpdateDate(syncTime);
          feedUser.setDeleted(true);
          feedUserDao.update(feedUser);
        }else{
          //create feed_user entitty for given id_user and id_feed
          FeedUser feedUserToSave = new FeedUser();
          feedUserToSave.setIdFeed(feedId);
          feedUserToSave.setIdUser(userId);
          feedUserToSave.setUpdateDate(syncTime);
          feedUserToSave.setDeleted(true);
          feedUserDao.create(feedUserToSave);
        }
      }
      return true;
    }
  }

  private long getUserIdWithToken(String token) throws SQLException{
    Sesion sesion = sesionDao.queryBuilder().where().eq(Sesion.COLUMN_TOKEN, token).queryForFirst();
    return (sesion != null) ? sesion.getIdUser() : 0;
  }
}

