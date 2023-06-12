package com.quotes;

import com.quotes.service.DatabaseService;
import com.quotes.service.LiquibaseService;
import com.quotes.service.WebService;

public class App {

    public static void main(String[] args) {
        DatabaseService.createDB();
        LiquibaseService.liquiBaseFunc();
        WebService.webserver();
    }
}
