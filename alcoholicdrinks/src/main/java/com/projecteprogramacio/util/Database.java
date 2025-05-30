package com.projecteprogramacio.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    // Ruta relativa a la base de dades a la mateixa carpeta on s'executa el JAR
    private static final String DB_URL = "jdbc:sqlite:alcoholic_drinks.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}
