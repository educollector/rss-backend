package com.ola;
import com.google.gson.Gson;
import com.ola.model.*;
import spark.ModelAndView;
import spark.Spark;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
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

        post("/login", "application/json", (req, res) -> {
            String body = req.body();

            User user = gson.fromJson(req.body(), User.class);
            String result = databaseManager.login(user);
            if(result == null){
                return "{\"message\":\"noUser\"}";
            }else{
                String json = "{\"message\":\"logged\", \"token\":\"" + result + "\"}";
                return json;
            }

        });

        //res.redirect("/orderfinished");
    }
}
