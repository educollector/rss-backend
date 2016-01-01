package com.ola.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by olaskierbiszewska on 08.11.15.
 */
public class Product {

  private static final String COLUMN_ID        = "ID";
  private static final String COLUMN_NAME      = "NAME";
  private static final String COLUMN_BRAND     = "BRAND";
  private static final String COLUMN_PRICE     = "PRICE";
  private static final String COLUMN_IMAGE_URL = "IMAGE_URL";
  private static final String COLUMN_DESCRIPTION = "DESCRIPTION";

  public static Product fromResultSet(ResultSet resultSet) throws SQLException {
    Product product = new Product();
    product.setId(resultSet.getInt(resultSet.findColumn(COLUMN_ID)));
    product.setName(resultSet.getString(resultSet.findColumn(COLUMN_NAME)));
    product.setBrand(resultSet.getString(resultSet.findColumn(COLUMN_BRAND)));
    product.setPrice(resultSet.getDouble(resultSet.findColumn(COLUMN_PRICE)));
    product.setImageUrl(resultSet.getString(resultSet.findColumn(COLUMN_IMAGE_URL)));
    product.setDescription(resultSet.getString(resultSet.findColumn(COLUMN_DESCRIPTION)));
    return product;
  }

  private int    id;
  private String name;
  private String brand;
  private String imageUrl;
  private double price;


  private String description;

  private String wasAddedToCart;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getDescription() { return description; }

  public void setDescription(String description) { this.description = description; }
}
