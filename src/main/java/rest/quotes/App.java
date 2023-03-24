package rest.quotes;
import io.javalin.Javalin;
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


public class App 




    {
        public static void main( String[] args ) throws JsonProcessingException, Exception{

            ExecutorService executor = Executors.newFixedThreadPool(1);
            Runnable testRunnableTask = () ->  {
                try{
                    pushandget();
                }
                catch(Exception e) {
                    System.out.println("Exception occured "+e);
                }

            };
            executor.execute(testRunnableTask); 
            
            Javalin app = Javalin.create().start(80);
                app.routes(() -> {
                    app.get("/", context -> {
                        // Make a call resulting in the initial completable future
                        // Return the result future
                        context.result(readquotesfromdb());
                    });
                });

            app.error(404, ctx -> ctx.result("Wello World"));


    }

    public static void webserver(){
        
    }

    public static String readquotesfromdb(){
        String connectionUrl =
                "jdbc:sqlserver://localhost:1433;"
                        + "database=quotes;"
                        + "user=sa;"
                        + "password=Admin123;"
                        + "encrypt=true;"
                        + "trustServerCertificate=true;"
                        + "loginTimeout=30;";
        
        
            // Create and execute a SELECT SQL statement.
            String readsql = "SELECT TOP 1 * FROM dbo.quotes ORDER BY NEWID()";
            try (Connection connection = DriverManager.getConnection(connectionUrl);
                Statement statement = connection.createStatement();) {
                ResultSet rs = statement.executeQuery(readsql);
                rs.next();
                String readid = rs.getString("id");
                String readcontent = rs.getString("content");
                String readauthor = rs.getString("author");
                String readauthorslug = rs.getString("authorslug");
                int readlength = rs.getInt("length");
                String readdateAdded = rs.getString("dateAdded");
                String jsonget = "{\"id\":"+readid+",\"content\":"+readcontent+", \"author\":"+readauthor+", \"authorSlug\":"+readauthorslug+", \"length\":"+readlength+", \"dateadded\":"+readdateAdded+"}";
                System.out.println(jsonget);
                return jsonget;

            } catch (SQLException e) {
                System.out.println(e);
                return e.getMessage();
            }
        }       


    public static void writequotestodb(String quoteid, String quotecontent, String quoteauthor, String quoteauthorslug, int quotelength, String quoteadddate){
        String connectionUrl =
                "jdbc:sqlserver://localhost:1433;"
                        + "database=quotes;"
                        + "user=sa;"
                        + "password=Admin123;"
                        + "encrypt=true;"
                        + "trustServerCertificate=true;"
                        + "loginTimeout=30;";

        ResultSet resultSet = null;

        try (Connection connection = DriverManager.getConnection(connectionUrl);
                Statement statement = connection.createStatement();) {
        
            // Create and execute a SELECT SQL statement.
            String selectSql = "Insert into dbo.quotes (id, content, author, authorSlug, length, dateadded) values ('" + quoteid + "','\"" + quotecontent +"\"','"+quoteauthor+"','" + quoteauthorslug + "','" + quotelength + "','" + quoteadddate+"')";
            resultSet = statement.executeQuery(selectSql);
        
                    // Print results from select statement
            while (resultSet.next()) {
                System.out.println(resultSet.getString(2) + " " + resultSet.getString(3));
            }
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
        }
    }

    public static void pushandget() throws Exception {
        while(true){
            try{
                URL url = new URL("https://api.quotable.io/random");
                InputStreamReader reader = new InputStreamReader(url.openStream());
                dtoinformation dto = new Gson().fromJson(reader, dtoinformation.class);


                //System.out.println(dto._id);
                String quoteid = (dto._id);
                //System.out.println(dto.content);
                String quotecontent = (dto.content.replace('\'', '#'));
                //System.out.println(dto.author);
                String quoteauthor = (dto.author);
                //System.out.println(dto.tags);
                //String quotetags = (dto.tags);
                //System.out.println(dto.authorSlug);
                String quoteauthorSlug = (dto.authorSlug);
                //System.out.println(dto.length);
                int quotelength = Integer.parseInt(dto.length);
                //System.out.println(dto.dateAdded);
                String quoteadddate = (dto.dateAdded);

                writequotestodb(quoteid,  quotecontent, quoteauthor, quoteauthorSlug, quotelength, quoteadddate);
            }
            catch (IOException httpe){
                if (httpe.getMessage().contains("429")){
                    Thread.sleep(10000);
                }
                else {
                    System.out.println("Unexpectet error in push and get");
                    System.out.println(httpe);
                }
            }

        }
    }
        
        private class dtoinformation {
            String _id;
            String content;
            String author;
            //String tags;
            String authorSlug;
            String length;
            String dateAdded;

        }
    }


