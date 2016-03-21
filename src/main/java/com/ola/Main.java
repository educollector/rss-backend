package com.ola;

import com.google.gson.Gson;
import com.ola.model.*;
import spark.Spark;

import javax.naming.AuthenticationException;
import java.sql.SQLException;
import java.util.List;

import static spark.Spark.*;

/**
 * Created by olaskierbiszewska on 24.10.15.
 */
public class Main {

    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String HEADER_AUTH = "Auth-Token";

    public static void main(String[] args) throws SQLException {

        DatabaseManager databaseManager = new DatabaseManager();
        Gson gson = new Gson();
        Spark.staticFileLocation("/static");

        // HTTPS
        // kestore generated with command:
        // keytool -keystore rss-server -alias rss-server -genkey -keyalg RSA)
        secure("ssl/rss-server", "password", null, null);

        // ROUTES

        post("/register", CONTENT_TYPE_JSON, (req, res) -> {
            String body = req.body();
            User user = gson.fromJson(body, User.class);
            String token = databaseManager.register(user);
            return StringsManager.stringRegistrationSucceedWithToken(token);
        });

        post("/login", CONTENT_TYPE_JSON, (req, res) -> {
            String body = req.body();
            User user = gson.fromJson(req.body(), User.class);
            String token = databaseManager.login(user);
            return StringsManager.stringUserLoggedWithToken(token);
        });

        post("/feeds", CONTENT_TYPE_JSON, (req, res) -> {
            // Body: list of feeds
            // action: add feeds to db (+delete/update as this is delta update)
            // return: list of feeds updated from last update (as this is atomic delta update)
            User authUser = databaseManager.authSession(req.headers(HEADER_AUTH));
            FeedRequest feedRequest = gson.fromJson(req.body(), FeedRequest.class);
            FeedRequest result = databaseManager.saveFeeds(feedRequest, authUser);
            return gson.toJson(result);
        });

        put("/feeds", CONTENT_TYPE_JSON, (req, res) -> {
            // body: feed json
            // action: add signle feed to database
            // return: Json representation with feed, or empty
            User authUser = databaseManager.authSession(req.headers(HEADER_AUTH));
            Feed feed = gson.fromJson(req.body(), Feed.class);
            databaseManager.create(feed, authUser);
            return Response.success();
        });

        get("/feeds", CONTENT_TYPE_JSON, (req, res) -> {
            // body: nil
            // action: read list of feeds for user
            // return: Json with list of feeds for user
            User authUser = databaseManager.authSession(req.headers(HEADER_AUTH));
            List<Feed> feeds = databaseManager.getFeeds(authUser);
            return gson.toJson(feeds);
        });

        delete("/feeds/:id", CONTENT_TYPE_JSON, (req, res) -> {
            // body: nil
            // action: delete feed with given ID
            // return: Json with status (??)
            User authUser = databaseManager.authSession(req.headers(HEADER_AUTH));
            String feedId = req.params(":id");
            databaseManager.deleteFeedById(feedId, authUser);
            return Response.success();
        });

        get("/feeds/:id", CONTENT_TYPE_JSON, (req, res) -> {
            // body: nil
            // action: read single feed with given id
            // return: json with single feed
            User authUser = databaseManager.authSession(req.headers(HEADER_AUTH));
            String feedId = req.params(":id");
            Feed feed = databaseManager.getFeedById(feedId, authUser);
            return gson.toJson(feed);
        });

        post("/feeds/:id", CONTENT_TYPE_JSON, (req, res) -> {
            // body: json with single feed
            // action: update feed with given id
            // return: Json with status (??)
            User authUser = databaseManager.authSession(req.headers(HEADER_AUTH));
            String feedId = req.params(":id");
            Feed feed = gson.fromJson(req.body(), Feed.class);
            databaseManager.updateFeedById(feedId, feed, authUser);
            return Response.success();
        });

        // FILTERS

        after((req, res) -> {
            res.type(CONTENT_TYPE_JSON);
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
