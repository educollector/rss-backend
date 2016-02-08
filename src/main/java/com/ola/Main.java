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
            String token = databaseManager.register(user);
            return StringsManager.stringRegistrationSucceedWithToken(token);
        });

        post("/login", "application/json", (req, res) -> {
            String body = req.body();
            User user = gson.fromJson(req.body(), User.class);
            String token = databaseManager.login(user);
            return StringsManager.stringUserLoggedWithToken(token);
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
