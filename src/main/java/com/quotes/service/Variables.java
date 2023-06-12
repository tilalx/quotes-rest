package com.quotes.service;

import java.io.File;
import io.github.cdimascio.dotenv.Dotenv;

public class Variables {

    public static boolean isDockerized() {
        File f = new File("/.dockerenv");
        return f.exists();
    }

    public static Dotenv dotenv = isDockerized()
            ? Dotenv.configure()
                    .directory("/config")
                    .ignoreIfMissing()
                    .load()
            : Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

    public static String pgconnectionUrl = "jdbc:postgresql://" + dotenv.get("PGHOST") + ":" + dotenv.get("PGPORT")
            + "/"
            + dotenv.get("PGDATABASE") + "?"
            + "user=" + dotenv.get("PGDBUSER") + "&"
            + "password=" + dotenv.get("PGDBPASSWORD") + "&"
            + "ssl=" + dotenv.get("PGSSL") + "&"
            + "sslmode=" + dotenv.get("PGSSLMODE") + "&"
            + "sslrootcert=" + dotenv.get("PGSSLROOTCERT") + "&"
            + "connectTimeout=" + dotenv.get("PGCONNECTTIMEOUT");

    public static String pgconnectionUrlwithoutPasswd = "jdbc:postgresql://" + dotenv.get("PGHOST") + ":"
            + dotenv.get("PGPORT") + "/"
            + dotenv.get("PGDATABASE") + "?"
            + "user=" + dotenv.get("PGDBUSER") + "&"
            + "ssl=" + dotenv.get("PGSSL") + "&"
            + "sslmode=" + dotenv.get("PGSSLMODE") + "&"
            + "sslrootcert=" + dotenv.get("PGSSLROOTCERT") + "&"
            + "connectTimeout=" + dotenv.get("PGCONNECTTIMEOUT");

    public static String pgconnectionUrlwithoutDB = "jdbc:postgresql://" + dotenv.get("PGHOST") + ":"
            + dotenv.get("PGPORT") + "/"
            + "?"
            + "user=" + dotenv.get("PGDBUSER") + "&"
            + "password=" + dotenv.get("PGDBPASSWORD") + "&"
            + "ssl=" + dotenv.get("PGSSL") + "&"
            + "sslmode=" + dotenv.get("PGSSLMODE") + "&"
            + "sslrootcert=" + dotenv.get("PGSSLROOTCERT") + "&"
            + "connectTimeout=" + dotenv.get("PGCONNECTTIMEOUT");

}
