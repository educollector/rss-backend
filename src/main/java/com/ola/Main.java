package com.ola;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ola.model.*;
import spark.Spark;

import java.sql.SQLException;
import java.util.UUID;

import static java.lang.Integer.parseInt;
import static spark.Spark.get;
import static spark.Spark.post;

/**
 * Created by olaskierbiszewska on 24.10.15.
 */
public class Main {

    public static void main(String[] args) throws SQLException {

        DatabaseManager databaseManager = new DatabaseManager();
        Gson gson = new Gson();
        String personJson = "{\"name\":\"Jan Kowalskki\"}";
        //Person personFromJson = gson.fromJson(personJson, Person.class);

        Spark.staticFileLocation("/static");

        //generate random UUIDs
        UUID idOne = UUID.randomUUID();
        UUID idTwo = UUID.randomUUID();
        System.out.println("UUID One: " + idOne);
        System.out.println("UUID Two: " + idTwo);


        post("/register", "application/json", (req, res) -> {
            String body = req.body();
            User user = gson.fromJson(req.body(), User.class);
            if(databaseManager.checkUserExistInDbByName(user)){
                return StringsManager.stringNickNotAvailable();
            }else{
                //nick is available, can register
                String result = databaseManager.register(user);
                if(result == null){
                    return StringsManager.stringRegistrationFailed();
                }else{
                    String json = StringsManager.stringRegistrationSucceedWithToken() + result + "\"}";
                    return json;
                }
            }
        });

        post("/login", "application/json", (req, res) -> {
            String body = req.body();

            User user = gson.fromJson(req.body(), User.class);
            String result = databaseManager.login(user);
            if(result == null){
                return StringsManager.stringInvalidNameOrPassword();
            }else{
                String json = StringsManager.stringUserLoggedWithToken() + result + "\"}";
                return json;
            }
        });

        post("/saveFeeds", "application/json", (req, res) -> {
            FeedRequest feedRequest = gson.fromJson(req.body(), FeedRequest.class);
            FeedRequest result = databaseManager.saveFeeds(feedRequest);
            if(result == null){
                return "fail";
            }else{
                return gson.toJson(result);
            }

        });
    }
}
