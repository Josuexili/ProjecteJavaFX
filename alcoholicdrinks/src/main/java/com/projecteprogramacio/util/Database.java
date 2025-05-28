package com.projecteprogramacio.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:/Users/josuegonzalezsantacreu/Desktop/ProjecteJavaFX/alcoholicdrinks/src/main/resources/alcoholic_drinks.db ";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}
