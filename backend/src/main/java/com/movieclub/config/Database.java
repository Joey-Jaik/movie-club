package com.movieclub.config;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        Dotenv dotenv = null;

         try {
            dotenv = Dotenv.load();
         }
         catch (Exception e) {
            // no .env file in production, use system environment variables
         }

         URL = dotenv != null ? dotenv.get("DB_URL") : System.getenv("DB_URL");
         USER = dotenv != null ? dotenv.get("DB_USER") : System.getenv("DB_USER");
         PASSWORD = dotenv != null ? dotenv.get("DB_PASSWORD") : System.getenv("DB_PASSWORD");
    }

    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }
}