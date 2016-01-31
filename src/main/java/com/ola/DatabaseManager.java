package com.ola;

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
    connect = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
  }

//  public List<Product> getAllProducts() throws SQLException {
//    List<Product> productList = new ArrayList<>();
//    PreparedStatement preparedStatement = connect.prepareStatement("SELECT * FROM PRODUCT");
//    ResultSet resultSet = preparedStatement.executeQuery();
//    while (resultSet.next()) {
//      productList.add(Product.fromResultSet(resultSet));
//    }
//    return productList;
//  }
//
//  public Product getProductById(int productId) throws SQLException {
//    List<Product> productList = new ArrayList<>();
//    PreparedStatement preparedStatement = connect.prepareStatement("SELECT * FROM PRODUCT where id=" + productId);
//    ResultSet resultSet = preparedStatement.executeQuery();
//    if (resultSet.next()) {
//      return Product.fromResultSet(resultSet);
//    }
//    throw new SQLException("Product with ID = " + productId + " not found.");
//  }
//
//  public Cart getCurrentCart() throws SQLException {
//    // Select cart itself
//    PreparedStatement preparedStatement = connect.prepareStatement("SELECT * FROM CART WHERE is_active=1");
//    ResultSet resultSet = preparedStatement.executeQuery();
//    if (resultSet.next()) {
//      Cart cart = Cart.fromResultSet(resultSet);
//
//      // Select cart items
//      List<CartItem> cartItems = getCartItems(cart);
//      cart.setCartItemsList(cartItems);
//      return cart;
//    } else {
//      PreparedStatement newCart = connect.prepareStatement("INSERT INTO CART (IS_ACTIVE) VALUES (1)");
//      newCart.executeUpdate();
//      return getCurrentCart();
//    }
//  }
//
//  public int getCurrentCartCount() throws SQLException {
//    PreparedStatement preparedStatement = connect.prepareStatement("SELECT SUM(CARTSPRODUCTS.AMOUNT)\n" +
//        "from CART \n" +
//        "INNER JOIN CARTSPRODUCTS on CARTSPRODUCTS.ID_CART=CART.ID\n" +
//        "where is_active=1");
//    ResultSet resultSet = preparedStatement.executeQuery();
//    return resultSet.next() ? resultSet.getInt(1) : 0;
//  }
//
//  public void addToCurrentCart(Product product) throws SQLException {
//    Cart cart = getCurrentCart();
//    String checkQuery = "SELECT * FROM CARTSPRODUCTS where id_cart=" + cart.getId() + " AND id_product=" + product.getId();
//    PreparedStatement preparedStatementCheck = connect.prepareStatement(checkQuery);
//    ResultSet resultSet = preparedStatementCheck.executeQuery();
//
//    Integer currentAmount = resultSet.next() ?
//        CartItem.fromResultSet(resultSet).getAmount() :
//        0;
//
//    String query;
//    if (currentAmount > 0) {
//      ++currentAmount;
//      query = "UPDATE CARTSPRODUCTS SET amount=" + currentAmount + " WHERE id_product=" + product.getId()
//          + " AND id_cart=" + cart.getId();
//    } else {
//      ++currentAmount;
//      query = "INSERT INTO CARTSPRODUCTS (id_product, amount, id_cart) VALUES ("
//          + product.getId() + "," + currentAmount + "," + cart.getId() + ")";
//    }
//    PreparedStatement preparedStatement = connect.prepareStatement(query);
//    preparedStatement.executeUpdate();
//  }
//
//  public List<CartItem> getCartItems(Cart cart) throws SQLException {
//    List<CartItem> cartItemList = new ArrayList<>();
//    PreparedStatement preparedStatement = connect.prepareStatement("SELECT * FROM CARTSPRODUCTS WHERE id_cart=" + cart.getId());
//    ResultSet resultSet = preparedStatement.executeQuery();
//    while (resultSet.next()) {
//      CartItem item = CartItem.fromResultSet(resultSet);
//      item.setProduct(getProductById(item.getProductId()));
//      cartItemList.add(item);
//    }
//    return cartItemList;
//  }
//
//  public Order placeOrder(Order order) throws SQLException {
//    PreparedStatement updateCart = connect.prepareStatement("UPDATE CART SET IS_ACTIVE = 0 WHERE ID = " + order.getCartId());
//    updateCart.executeUpdate();
//
//    PreparedStatement insertOrder = connect.prepareStatement(
//        String.format("INSERT INTO ORDERS (%1$s, %3$s, %5$s, %7$s, %9$s, %11$s, %13$s, %15$s) " +
//                "VALUES (%2$d, '%4$s', '%6$s', '%8$s', '%10$s', '%12$s', '%14$s', '%16$s')",
//            Order.COLUMN_ID_CART, order.getCartId(),
//            Order.COLUMN_EMAIL, order.getEmail(),
//            Order.COLUMN_TYP_DOSTAWY, order.getTypDostawy(),
//            Order.COLUMN_TYP_ODBIORCY, order.getTypOdbiorcy(),
//            Order.COLUMN_NAZWA, order.getNazwa(),
//            Order.COLUMN_ADRES, order.getAdres(),
//            Order.COLUMN_CITY, order.getCity(),
//            Order.COLUMN_POSTAL_CODE, order.getPostalCode()));
//    insertOrder.executeUpdate();
//    return order;
//  }

  //RSS

  public String login (User user) throws SQLException{
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

  public boolean checkUserExistInDb(User user) throws SQLException{
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
    //TODO return token or null
    return  token.toString();
  }

  public void deleteExpiredSesions() throws SQLException{
    String query = "DELETE FROM SESION WHERE (EXP_DATE < " + System.currentTimeMillis()+" AND ID_USER<>0);";
    PreparedStatement statement = connect.prepareStatement(query);
    statement.executeUpdate();
  }

}

