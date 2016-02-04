package com.ola.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Data;

/**
 * Created by olaskierbiszewska on 04.02.16.
 */
@DatabaseTable(tableName = FeedUser.TABLE_NAME)
@Data
public class FeedUser {
    public static final String TABLE_NAME = "USER_FEED";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_ID_USER = "ID_USER";
    public static final String COLUMN_ID_FEED = "ID_FEED";
    public static final String COLUMN_ID_DELETED = "ID_DELETED";
    public static final String COLUMN_UPDATE_DATE = "UPDATE_DATE";

    @DatabaseField(columnName = COLUMN_ID, generatedId = true)
    private Long id;
    @DatabaseField(columnName = COLUMN_ID_USER)
    private Long idUser;
    @DatabaseField(columnName = COLUMN_ID_FEED)
    private Long idFeed;
    @DatabaseField(columnName = COLUMN_ID_DELETED)
    private Bool isDeleted;
    @DatabaseField(columnName = COLUMN_UPDATE_DATE)
    private Long updateDate;
}
