package com.ola.model;

import spark.Request;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 11/29/15.
 */
public class Order {

  public static final String COLUMN_ID           = "ID";
  public static final String COLUMN_ID_CART      = "ID_CART";
  public static final String COLUMN_EMAIL        = "EMAIL";
  public static final String COLUMN_TYP_DOSTAWY  = "TYP_DOSTAWY";
  public static final String COLUMN_TYP_ODBIORCY = "TYP_ODBIORCY";
  public static final String COLUMN_NAZWA        = "NAZWA";
  public static final String COLUMN_ADRES        = "ADRES";
  public static final String COLUMN_CITY         = "CITY";
  public static final String COLUMN_POSTAL_CODE  = "POSTAL_CODE";

  public static Order fromRequest(Request req, Cart cart) {
    Order order = new Order();
    order.cartId = cart.getId();
    order.email = req.queryParams("inputMail");
    order.typDostawy = req.queryParams("dostawaRadios");
    order.typOdbiorcy = req.queryParams("typeRadios");
    order.nazwa = req.queryParams("inputName");
    order.adres = req.queryParams("inputAddress");
    order.city = req.queryParams("inputCity");
    order.postalCode = req.queryParams("inputPostalCode");
    return order;
  }

  public static Order fromResultSet(ResultSet resultSet) throws SQLException {
    Order order = new Order();
    order.id = resultSet.getInt(resultSet.findColumn(COLUMN_ID));
    order.cartId = resultSet.getInt(resultSet.findColumn(COLUMN_ID_CART));
    order.email = resultSet.getString(resultSet.findColumn(COLUMN_EMAIL));
    order.typDostawy = resultSet.getString(resultSet.findColumn(COLUMN_TYP_DOSTAWY));
    order.typOdbiorcy = resultSet.getString(resultSet.findColumn(COLUMN_TYP_ODBIORCY));
    order.nazwa = resultSet.getString(resultSet.findColumn(COLUMN_NAZWA));
    order.adres = resultSet.getString(resultSet.findColumn(COLUMN_ADRES));
    order.city = resultSet.getString(resultSet.findColumn(COLUMN_CITY));
    order.postalCode = resultSet.getString(resultSet.findColumn(COLUMN_POSTAL_CODE));
    return order;
  }

  private int    id;
  private int    cartId;
  private String email;
  private String typDostawy;
  private String typOdbiorcy;
  private String nazwa;
  private String adres;
  private String city;
  private String postalCode;

  public int getId() {
    return id;
  }

  public int getCartId() {
    return cartId;
  }

  public String getEmail() {
    return email;
  }

  public String getTypDostawy() {
    return typDostawy;
  }

  public String getTypOdbiorcy() {
    return typOdbiorcy;
  }

  public String getNazwa() {
    return nazwa;
  }

  public String getAdres() {
    return adres;
  }

  public String getCity() {
    return city;
  }

  public String getPostalCode() {
    return postalCode;
  }
}
