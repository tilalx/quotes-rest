package com.quotes.service;

import org.json.JSONArray;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

public class WebService {

    public static void webserver() {

        Javalin.create(config -> {
            config.jetty.port = 80;
            config.staticFiles.add("/public", Location.CLASSPATH);
            config.http.generateEtags = true;
            config.http.prefer405over404 = true;
            config.http.maxRequestSize = 1000;
            config.http.defaultContentType = "application/json";
            config.http.asyncTimeout = 10_000L;

            config.routes.before(ctx -> {
                ctx.header("ACCESS_CONTROL_ALLOW_CREDENTIALS", "true");

                System.out.print("Client: " + ctx.ip());
                System.out.print(" Status: " + ctx.status());
                System.out.print(" Path: " + ctx.fullUrl());
                System.out.println();
            });

            config.routes.get("/random", ctx -> {
                String countParam = ctx.queryParam("count");
                int count = countParam != null ? Integer.parseInt(countParam) : 5;

                JSONArray jsonArray = DatabaseService.randomQuote(count);

                ctx.result(jsonArray.toString());
                ctx.contentType("application/json");
            });

            config.routes.get("/", ctx -> ctx.redirect("/index.html"));

            config.routes.error(404, ctx -> ctx.result("404"));

        }).start();
    }
}