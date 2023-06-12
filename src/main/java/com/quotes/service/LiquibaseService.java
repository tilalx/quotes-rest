package com.quotes.service;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import java.sql.Connection;
import java.sql.DriverManager;
import liquibase.database.Database;

public class LiquibaseService {

    public static void liquiBaseFunc() {
        try {
            // Create a database connection
            Connection connection = DriverManager.getConnection(Variables.pgconnectionUrlwithoutPasswd,
                    Variables.dotenv.get("PGDBUSER"), Variables.dotenv.get("PGDBPASSWORD"));

            // Create a Liquibase instance
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase("liquibase/changelog.xml", new ClassLoaderResourceAccessor(),
                    database);

            // Set additional contexts
            Contexts contexts = new Contexts();

            // Apply the database changes with contexts
            liquibase.update(contexts);

            // Close the Liquibase instance
            liquibase.close();

            // Close the connection
            connection.close();

            System.out.println("Database migration successful!");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
