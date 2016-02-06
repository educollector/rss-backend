package com.ola.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;

/**
 * Created by olaskierbiszewska on 04.02.16.
 */
@DatabaseTable(tableName = Sesion.TABLE_NAME)
@Data
public class Sesion {
    public static final String TABLE_NAME = "SESION";
    public static final String COLUMN_ID_USER = "ID_USER";
    public static final String COLUMN_TOKEN = "TOKEN";
    public static final String COLUMN_EXP_DATE= "EXP_DATE";

    @DatabaseField(columnName = COLUMN_ID_USER)
    private Long idUser;
    @DatabaseField(columnName = COLUMN_TOKEN)
    private String token;
    @DatabaseField(columnName = COLUMN_EXP_DATE)
    private Long expDate;
}
