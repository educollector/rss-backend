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

        // ROUTES

        post("/register", "application/json", (req, res) -> {
            String body = req.body();
            User user = gson.fromJson(body, User.class);
            String token = databaseManager.register(user);
            return StringsManager.stringRegistrationSucceedWithToken(token);
        });

        post("/login", "application/json", (req, res) -> {
            String body = req.body();
            User user = gson.fromJson(req.body(), User.class);
            String token = databaseManager.login(user);
            return StringsManager.stringUserLoggedWithToken(token);
        });

        post("/feeds", "application/json", (req, res) -> {
            // Body: list of feeds
            // action: add feeds to db (+delete/update as this is delta update)
            // return: list of feeds updated from last update (as this is atomic delta update)
            User authUser = databaseManager.authSession(req.headers("Auth-Token"));
            FeedRequest feedRequest = gson.fromJson(req.body(), FeedRequest.class);
            FeedRequest result = databaseManager.saveFeeds(feedRequest, authUser);
            return gson.toJson(result);
        });

        put("/feeds", "application/json", (req, res) -> {
            // body: feed json
            // action: add signle feed to database
            // return: Json representation with feed, or empty
            return "";
        });

        get("/feeds", "application/json", (req, res) -> {
            // body: nil
            // action: read list of feeds for user
            // return: Json with list of feeds for user
            return "";
        });

        delete("/feeds/{id}", "application/json", (req, res) -> {
            // body: nil
            // action: delete feed with given ID
            // return: Json with status (??)
            return "";
        });

        get("/feeds/{id}", "application/json", (req, res) -> {
            // body: nil
            // action: read single feed with given id
            // return: json with single feed
            String feedId = req.params("id");
           // Feed feed = databaseManager.getFeedForUser(feedId);
            return "";// gson.toJson(feed);
        });

        post("/feeds/{id}", "application/json", (req, res) -> {
            // body: json with single feed
            // action: update feed with given id
            // return: Json with status (??)
            return "";
        });

        // FILTERS

        after((req, res) -> {
            res.type("application/json");
        });

        exception(AuthenticationException.class, (e, req, res) -> {
            res.status(401);
            res.body(e.getMessage());
        });

        exception(Exception.class, (e, req, res) -> {
            res.status(500);
            res.body(e.getMessage());
        });
    }
}
