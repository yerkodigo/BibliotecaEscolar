package com.bibliotecaescolar.main;

import com.bibliotecaescolar.config.DatabaseConnection;
import com.bibliotecaescolar.vista.VentanaLogin;

import javax.swing.*;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            DatabaseConnection conexionBD = DatabaseConnection.getInstance();
            conexionBD.getConnection();
            System.out.println("Conexión exitosa a la base de datos.");
            Runtime.getRuntime().addShutdownHook(new Thread(conexionBD::cerrarConexion));
            SwingUtilities.invokeLater(VentanaLogin::new);
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "No se pudo conectar con la base de datos biblioteca.\n"
                            + "Verifique que MySQL esté iniciado y que los datos de DatabaseConnection sean correctos.\n\n"
                            + "Detalle: " + e.getMessage(),
                    "Error de conexión",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
