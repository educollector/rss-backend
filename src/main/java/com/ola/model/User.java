package com.ola.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by olaskierbiszewska on 31.12.15.
 */
public class User {

    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_NAME = "NAME";
    private static final String COLUMN_PASSWORD = "PASSWORD";

    public static User fromResultSet(ResultSet resultSet) throws SQLException {
        User user = new User();
        if (resultSet.next()) {
            user.setId(resultSet.getInt(resultSet.findColumn(COLUMN_ID)));
            user.setName(resultSet.getString(resultSet.findColumn(COLUMN_NAME)));
            user.setPassword(resultSet.getString(resultSet.findColumn(COLUMN_PASSWORD)));
        }
        return user;
    }


    private Integer id;
    private String name;
    private String password;

    public User() {
    }

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }
}
