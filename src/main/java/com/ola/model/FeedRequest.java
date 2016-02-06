package com.ola.model;

import lombok.Data;
import java.util.List;

/**
 * Created by olaskierbiszewska on 05.02.16.
 */
@Data
public class FeedRequest {
    private String timestamp;
    private String token;
    private List<String> createdUpdated;
    private List<String> deleted;

}
