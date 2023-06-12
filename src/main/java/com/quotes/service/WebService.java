package com.quotes.service;

import org.json.JSONArray;
import io.javalin.Javalin;

public class WebService {

    public static void webserver() {

        Javalin app = Javalin.create(config -> {
            // config.staticFiles("public");
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/";
                staticFiles.precompress = false;
                config.http.generateEtags = true; // if javalin should generate etags for dynamic responses (not static
                config.http.prefer405over404 = true; // return 405 instead of 404 if path is mapped to different HTTP
                config.http.maxRequestSize = 1000; // the max size of request body that can be accessed without using
                config.http.defaultContentType = "application/json"; // the default content type
                config.http.asyncTimeout = 10_000L;
                config.routing.ignoreTrailingSlashes = true;

            });

        }).start(80);

        app.before(ctx -> {
            ctx.header("header.ACCESS_CONTROL_ALLOW_CREDENTIHALS", "true");
            System.out.print("Client: " + ctx.ip());
            System.out.print(" Status: " + ctx.status());
            System.out.print(" Path: " + ctx.fullUrl());
            System.out.println("");
        });

        app.routes(() -> {
            app.get("/random", ctx -> {
                String countParam = ctx.queryParam("count");
                int count = countParam != null ? Integer.parseInt(countParam) : 5; // Get the value of 'count'
                JSONArray jsonArray = DatabaseService.randomQuote(count); // Get a JSON array of report IDs for the user
                ctx.json(jsonArray.toString()).contentType("application/json"); // Send the JSON array to the client
            });

            app.get("/", ctx -> {
                ctx.redirect("/index.html");
            });
        });

        app.error(404, ctx ->

        {

            ctx.result("404");
        });

    }

}
