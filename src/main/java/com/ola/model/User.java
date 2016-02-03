package com.ola.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by olaskierbiszewska on 31.12.15.
 */
@DatabaseTable(tableName = User.TABLE_NAME)
@Data
public class User {
    public static final String TABLE_NAME = "USER";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_NAME = "NAME";
    public static final String COLUMN_PASSWORD = "PASSWORD";

    @DatabaseField(columnName = COLUMN_ID, generatedId = true)
    private Long id;
    @DatabaseField(columnName = COLUMN_NAME)
    private String name;
    @DatabaseField(columnName = COLUMN_PASSWORD)
    private String password;
}
