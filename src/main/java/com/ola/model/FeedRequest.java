package com.ola.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import com.google.gson.annotations.Expose;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by olaskierbiszewska on 05.02.16.
 */
@Data
public class FeedRequest {
    @SerializedName("timestamp") private Long timestamp;
    @Expose private String token;
    @Expose private ArrayList<String> createdUpdated;
    @Expose private ArrayList<String> deleted;

}
