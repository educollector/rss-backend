package com.ola.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by olaskierbiszewska on 14.11.15.
 */
public class Cart {

    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_IS_ACTIVE = "IS_ACTIVE";

    public static Cart fromResultSet(ResultSet resultSet) throws SQLException {
        Cart cart = new Cart();
        cart.setId(resultSet.getInt(resultSet.findColumn(COLUMN_ID)));
        cart.setActive(resultSet.getInt(resultSet.findColumn(COLUMN_IS_ACTIVE)));
        return cart;
    }

    private int id;
    private int isActive;

    private List<CartItem> cartItemsList = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int isActive() {
      return isActive;
    }

    public void setActive(int isActive) {
      this.isActive = isActive;
    }

    public List<CartItem> getCartItemsList() {
        return cartItemsList;
    }

    public void setCartItemsList(List<CartItem> cartItemsList) {
        this.cartItemsList = cartItemsList;
    }

    public double getCartValue() {
        double value = 0;
        for(CartItem item : this.cartItemsList){
            value += item.getValue();
        }
        return value;
    }
}
