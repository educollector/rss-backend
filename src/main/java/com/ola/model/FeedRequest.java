package com.ola.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by olaskierbiszewska on 05.02.16.
 */
@Data
public class FeedRequest {
    private Long timestamp;
    private String token;
    private ArrayList<String> createdUpdated;
    private ArrayList<String> deleted;

}
