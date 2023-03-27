package com.quotes;

import io.javalin.*;
//import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.InputStreamReader;
import java.net.URL;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.concurrent.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.sql.Timestamp;
import io.github.cdimascio.dotenv.Dotenv;


public class App{

    
    public static Dotenv dotenv = Dotenv.configure()
            .load();
    
    public static String connectionUrl = "jdbc:sqlserver://"+dotenv.get("MSSQLIP")+":"+dotenv.get("MSSQLPORT")+";"
            
            + "database="+dotenv.get("DATABASE")+";"
            + "user="+dotenv.get("USER")+";"
            + "password="+dotenv.get("PASSWORD")+";"
            + "encrypt="+dotenv.get("ENCRYPT")+";"
            + "trustServerCertificate="+dotenv.get("TRUSTSERVERCERTOFOCATE")+";"
            + "loginTimeout="+dotenv.get("LOGINTIMEOUT")+";";

            public class Quote {
                private String _id;
                private String content;
                private String author;
                private List<String> tags;
                private String authorSlug;
                private int length;
                private String dateAdded;
            }

    


    public static void main(String[] args) throws JsonProcessingException, Exception {
        firstrun();
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Runnable testRunnableTask = () -> {
            try {
                pushandget();
            } catch (Exception e) {
                System.out.println("Exception occured " + e);
            }

        };
        executor.execute(testRunnableTask);

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
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(randomquote());

                ctx.json(jsonNode);

            });

            app.get("/", ctx -> {
                ctx.redirect("/index.html");
            });
        });


        

        app.error(404, ctx -> {

            ctx.result("404");
        });

    }

    public static void firstrun() throws Exception{
        try (Connection connection = DriverManager.getConnection(connectionUrl);
            Statement statement = connection.createStatement();) {
            String selectSql =  "IF OBJECT_ID(N'dbo.quotes', N'U') IS NULL Create table dbo.quotes ( id nchar (30) UNIQUE NOT NULL, content nchar (500) NOT NULL, author nchar (50) NOT NULL, tags nchar (100) NULL, authorSlug nchar (50) NOT NULL, length int NOT NULL, dateadded DATETIME NOT NULL, datemodified DATETIME NOT NULL, )";
            statement.execute(selectSql);
        }
    }   

    public static String randomquote() {

        String readsql = "SELECT TOP 1 * FROM dbo.quotes ORDER BY NEWID()";
        try (Connection connection = DriverManager.getConnection(connectionUrl);
                Statement statement = connection.createStatement();) {
            ResultSet rs = statement.executeQuery(readsql);
            rs.next();
            String readid = rs.getString("id").trim().replace("\"", "");
            String readcontent = rs.getString("content").trim().replace("$", "'").replace("^\"|\"$", "");
            String readauthor = rs.getString("author").trim().replaceAll("\"", "");
            String readtags = rs.getString("tags").trim().replaceAll("\"", "");
            String readauthorslug = rs.getString("authorslug").trim().replaceAll("\"", "");
            int readlength = rs.getInt("length");
            String readdateAdded = rs.getString("dateAdded").trim();
            String readdateModified = rs.getString("datemodified").trim();
            String jsonget = "{\"id\":\"" + readid + "\",\"content\":" + readcontent + ", \"author\":\"" + readauthor
                    + "\",  \"tags\":\"" + readtags + "\",\"authorSlug\":\"" + readauthorslug + "\", \"length\":\"" + readlength + "\", \"dateadded\":\"" + readdateAdded + "\", \"dateModified\":\""
                    + readdateModified + "\"}";
            connection.close();
            return jsonget;

        } catch (SQLException e) {
            System.out.println(e);
            return e.getMessage();
        }
    }

    public static void writequotestodb( String quoteid, String quotecontent, String quoteauthor, List<String> quotetags, String quoteauthorSlug, int quotelength, String quoteadddate) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        try (Connection connection = DriverManager.getConnection(connectionUrl);
            Statement statement = connection.createStatement();) {
            String selectSql = "Insert into dbo.quotes (id, content, author, tags, authorSlug, length, dateadded, datemodified) values ('"
                    + quoteid + "','\"" + quotecontent + "\"','" + quoteauthor + "', '" + quotetags + "', '" + quoteauthorSlug + "','"
                    + quotelength + "','" + quoteadddate + "', '" + timestamp + "')";
            ResultSet resultSet = statement.executeQuery(selectSql);
            System.out.println(resultSet);
            connection.close();
        }
        
        catch (SQLException e) {
        }
    }

    public static void pushandget() throws Exception {
        while (true) {
            try {
                URL url = new URL("https://api.quotable.io/random");
                InputStreamReader reader = new InputStreamReader(url.openStream());
                Gson gson = new Gson();
                Quote quote = gson.fromJson(reader, Quote.class);

                // System.out.println(dto._id);
                String quoteid = (quote._id);
                // System.out.println(dto.content);
                String quotecontent = (quote.content.replace('\'', '$'));
                // System.out.println(dto.author);
                String quoteauthor = (quote.author);
                // System.out.println(dto.tags);
                //String quotetags = String.join(",", quote.tags);
                List<String> quotetags = (quote.tags);
                // System.out.println(dto.authorSlug);
                String quoteauthorSlug = (quote.authorSlug);
                // System.out.println(dto.length);
                int quotelength = (quote.length);
                // System.out.println(dto.dateAdded);
                String quoteadddate = (quote.dateAdded);
                // System.out.println(quote.dateModified);
                //String quotedateModified = (quote.dateModified);
                
                writequotestodb(quoteid, quotecontent, quoteauthor, quotetags, quoteauthorSlug, quotelength, quoteadddate);
            } catch (IOException httpe) {
                if (httpe.getMessage().contains("429")) {
                    Thread.sleep(10000);
                } else {
                    System.out.println("Unexpectet error in push and get");
                    System.out.println(httpe);
                }
            }

        }
    }

}
