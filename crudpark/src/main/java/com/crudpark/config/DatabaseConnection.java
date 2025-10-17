package com.crudpark.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static final Properties PROPERTIES = new Properties();

    static {
        // Carga el archivo db.properties
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            PROPERTIES.load(input);
        } catch (IOException ex) {
            // Manejo simple de error si el archivo no existe o falla la lectura
            System.err.println("Error al cargar db.properties: " + ex.getMessage());
            // Nota: Podrías lanzar una RuntimeException aquí para detener la aplicación si es crítica.
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Error al cargar el driver de PostgreSQL: " + e.getMessage());
        }

        String url = PROPERTIES.getProperty("db.url");
        String user = PROPERTIES.getProperty("db.user");
        String password = PROPERTIES.getProperty("db.password");

        return DriverManager.getConnection(url, user, password);
    }

    // Método para cerrar recursos
    public static void closeConnection(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) { /* Ignorar errores al cerrar */ }
            }
        }
    }
}