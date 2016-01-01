package com.ola.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by olaskierbiszewska on 14.11.15.
 */
public class CartItem {

    private static final String COLUMN_ID_PRODUCT = "ID_PRODUCT";
    private static final String COLUMN_ID_CART = "ID_CART";
    private static final String COLUMN_AMOUNT = "AMOUNT";

    public static CartItem fromResultSet(ResultSet resultSet) throws SQLException {
        CartItem cartItem = new CartItem();
        cartItem.setProductId(resultSet.getInt(resultSet.findColumn(COLUMN_ID_PRODUCT)));
        cartItem.setCartId(resultSet.getInt(resultSet.findColumn(COLUMN_ID_CART)));
        cartItem.setAmount(resultSet.getInt(resultSet.findColumn(COLUMN_AMOUNT)));
        return cartItem;
    }

    private int productId;
    private int cartId;
    private int amount;

    // Relation (FK)
    private Product product;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getName() {
        return product.getName();
    }

    public double getPrice() {
        return product.getPrice();
    }

    public double getValue() {
        double finalValue = Math.round( product.getPrice() * amount * 100.0 ) / 100.0;
        return finalValue;
    }

    public void setProduct(Product product) {
      this.product = product;
    }
}

