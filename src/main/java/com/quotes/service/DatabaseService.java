package com.quotes.service;

import org.json.JSONArray;
import org.json.JSONObject;
import java.sql.*;

public class DatabaseService {

    public static JSONArray randomQuote(int count) {
        JSONArray jsonArray = new JSONArray();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(Variables.pgconnectionUrl);
            String query = "SELECT id, content, author, tags, authorSlug, length, dateadded, datemodified FROM quotes ORDER BY random() LIMIT ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, count);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String content = resultSet.getString("content");
                String author = resultSet.getString("author");
                String tags = resultSet.getString("tags");
                String authorSlug = resultSet.getString("authorSlug");
                int length = resultSet.getInt("length");
                Date dateadded = resultSet.getDate("dateadded");
                Date datemodified = resultSet.getDate("datemodified");

                JSONObject jsonObj = new JSONObject();
                jsonObj.put("id", id);
                jsonObj.put("content", content);
                jsonObj.put("author", author);
                jsonObj.put("tags", tags);
                jsonObj.put("authorSlug", authorSlug);
                jsonObj.put("length", length);
                jsonObj.put("dateadded", dateadded.toString());
                jsonObj.put("datemodified", datemodified.toString());

                jsonArray.put(jsonObj);
            }

            return jsonArray;
        } catch (SQLException e) {
            e.printStackTrace();
            return new JSONArray();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createDB() {
        String databaseName = Variables.dotenv.get("PGDATABASE");

        try (Connection conn = DriverManager.getConnection(Variables.pgconnectionUrlwithoutDB);
                Statement stmt = conn.createStatement();) {
            String checkDbExistsQuery = "SELECT 1 FROM pg_catalog.pg_database WHERE datname = '" + databaseName + "'";
            ResultSet resultSet = stmt.executeQuery(checkDbExistsQuery);

            if (!resultSet.next()) {
                String createDbQuery = "CREATE DATABASE " + databaseName;
                stmt.executeUpdate(createDbQuery);
                System.out.println("Database created successfully");
            } else {
                System.out.println("Database already exists");
            }

        } catch (SQLException e) {
            System.out.println("Failed to create database: " + e.getMessage());
        }
    }

}
