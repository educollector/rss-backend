package com.ola.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by olaskierbiszewska on 04.02.16.
 */
@DatabaseTable(tableName = FeedUser.TABLE_NAME)
public class Feed {
    public static final String TABLE_NAME = "USER_FEED";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_URL= "URL";
    public static final String COLUMN_SYNC_TIMESTAMP= "SYNC_TIMESTAMP";

    @DatabaseField(columnName = COLUMN_ID, generatedId = true)
    private Long id;
    @DatabaseField(columnName = COLUMN_URL)
    private String url;
    @DatabaseField(columnName = COLUMN_SYNC_TIMESTAMP)
    private String syncTimestamp;
}
