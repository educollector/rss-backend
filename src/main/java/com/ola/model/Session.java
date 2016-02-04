package com.ola.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;

/**
 * Created by olaskierbiszewska on 04.02.16.
 */
@DatabaseTable(tableName = Session.TABLE_NAME)
@Data
public class Session {
    public static final String TABLE_NAME = "SESSION";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_TOKEN = "TOKEN";
    public static final String COLUMN_EXP_DATE= "EXP_DATE";

    @DatabaseField(columnName = COLUMN_ID, generatedId = true)
    private Long id;
    @DatabaseField(columnName = COLUMN_TOKEN)
    private String token;
    @DatabaseField(columnName = COLUMN_EXP_DATE)
    private Long expDate;
}
