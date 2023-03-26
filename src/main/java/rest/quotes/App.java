package rest.quotes;

import io.javalin.*;
import com.fasterxml.jackson.core.JsonProcessingException;
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

public class App

{

    public static String connectionUrl = "jdbc:sqlserver://172.20.10.121:1433;"
            + "database=quotes;"
            + "user=sa;"
            + "password=qHYFqKoY4;"
            + "encrypt=true;"
            + "trustServerCertificate=true;"
            + "loginTimeout=30;";

    private class dtoinformation {
        String _id;
        String content;
        String author;
        // String tags;
        String authorSlug;
        String length;
        String dateAdded;

    }

    public static void main(String[] args) throws JsonProcessingException, Exception {

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
                config.http.generateEtags = true; // if javalin should generate etags for dynamic responses (not static
                                                  // files)
                config.http.prefer405over404 = true; // return 405 instead of 404 if path is mapped to different HTTP
                                                     // method
                config.http.maxRequestSize = 1000; // the max size of request body that can be accessed without using
                                                   // using an InputStream
                config.http.defaultContentType = "application/json"; // the default content type
                config.http.asyncTimeout = 3000;
            });

        }).start(80);

        app.before(ctx -> {
            ctx.header("Header.ACCESS_CONTROL_ALLOW_CREDENTIALS", "true");
            System.out.print("Client: " + ctx.ip());
            System.out.print(" Status: " + ctx.status());
            System.out.print(" Path: " + ctx.path());
            System.out.println("");
        });

        app.routes(() -> {
            app.get("/random", ctx -> {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(readquotesfromdb());

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

    public static String readquotesfromdb() {

        // Create and execute a SELECT SQL statement.
        String readsql = "SELECT TOP 1 * FROM dbo.quotes ORDER BY NEWID()";
        try (Connection connection = DriverManager.getConnection(connectionUrl);
                Statement statement = connection.createStatement();) {
            ResultSet rs = statement.executeQuery(readsql);
            rs.next();
            String readid = rs.getString("id").trim().replace("\"", "");
            String readcontent = rs.getString("content").trim().replace("$", "'").replace("^\"|\"$", "");
            String readauthor = rs.getString("author").trim().replaceAll("\"", "");
            String readauthorslug = rs.getString("authorslug").trim().replaceAll("\"", "");
            int readlength = rs.getInt("length");
            String readdateAdded = rs.getString("dateAdded").trim();
            String jsonget = "{\"id\":\"" + readid + "\",\"content\":" + readcontent + ", \"author\":\"" + readauthor
                    + "\", \"authorSlug\":\"" + readauthorslug + "\", \"length\":" + readlength + ", \"dateadded\":\""
                    + readdateAdded + "\"}";

            return jsonget;

        } catch (SQLException e) {
            System.out.println(e);
            return e.getMessage();
        }
    }

    public static void writequotestodb(String quoteid, String quotecontent, String quoteauthor, String quoteauthorslug,
            int quotelength, String quoteadddate) {

        try (Connection connection = DriverManager.getConnection(connectionUrl);
                Statement statement = connection.createStatement();) {

            // Create and execute a SELECT SQL statement.
            String selectSql = "Insert into dbo.quotes (id, content, author, authorSlug, length, dateadded) values ('"
                    + quoteid + "','\"" + quotecontent + "\"','" + quoteauthor + "','" + quoteauthorslug + "','"
                    + quotelength + "','" + quoteadddate + "')";
            ResultSet resultSet = statement.executeQuery(selectSql);
            System.out.println(resultSet);
            // Print results from select statement
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
        }
    }

    public static void pushandget() throws Exception {
        while (true) {
            try {
                URL url = new URL("https://api.quotable.io/random");
                InputStreamReader reader = new InputStreamReader(url.openStream());
                dtoinformation dto = new Gson().fromJson(reader, dtoinformation.class);

                // System.out.println(dto._id);
                String quoteid = (dto._id);
                // System.out.println(dto.content);
                String quotecontent = (dto.content.replace('\'', '$'));
                // System.out.println(dto.author);
                String quoteauthor = (dto.author);
                // System.out.println(dto.tags);
                // String quotetags = (dto.tags);
                // System.out.println(dto.authorSlug);
                String quoteauthorSlug = (dto.authorSlug);
                // System.out.println(dto.length);
                int quotelength = Integer.parseInt(dto.length);
                // System.out.println(dto.dateAdded);
                String quoteadddate = (dto.dateAdded);

                writequotestodb(quoteid, quotecontent, quoteauthor, quoteauthorSlug, quotelength, quoteadddate);
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
