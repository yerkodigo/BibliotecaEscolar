package com.bibliotecaescolar.main;

import com.bibliotecaescolar.controlador.ConexionBD;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try (Connection conn = ConexionBD.obtenerConexion()) {
            System.out.println("Conexión exitosa a la base de datos.");
//            SwingUtilities.invokeLater(VentanaPrincipal::new);
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "No se pudo conectar con la base de datos speedfast_db.\n"
                            + "Verifique que MySQL esté iniciado y que los datos de ConexionBD sean correctos.\n\n"
                            + "Detalle: " + e.getMessage(),
                    "Error de conexión",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
