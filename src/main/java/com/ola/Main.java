package com.ola;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ola.model.*;
import spark.Spark;

import javax.naming.AuthenticationException;
import java.sql.SQLException;
import java.util.UUID;

import static java.lang.Integer.parseInt;
import static spark.Spark.*;

/**
 * Created by olaskierbiszewska on 24.10.15.
 */
public class Main {

    public static void main(String[] args) throws SQLException {

        DatabaseManager databaseManager = new DatabaseManager();
        Gson gson = new Gson();
        Spark.staticFileLocation("/static");

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

        post("/syncFeed", "application/json", (req, res) -> {
            FeedRequest feedRequest = gson.fromJson(req.body(), FeedRequest.class);
            User authUser = databaseManager.authSession(feedRequest.getToken());
            FeedRequest result = databaseManager.saveFeeds(feedRequest, authUser);
            return gson.toJson(result);
        });

        exception(AuthenticationException.class, (e, req, res) -> {
            res.status(401);
            res.body(e.getMessage());
        });
    }
}
