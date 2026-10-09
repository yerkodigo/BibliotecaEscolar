package com.bibliotecaescolar.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "1q2w3e4r5t";

    private static DatabaseConnection instancia;
    private Connection conexion;

    private DatabaseConnection() throws SQLException {
        conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }

    public static synchronized DatabaseConnection getInstance() throws SQLException {
        if (instancia == null) {
            instancia = new DatabaseConnection();
        }
        return instancia;
    }

    public synchronized Connection getConnection() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
        }
        return conexion;
    }

    public synchronized void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}
