package com.bibliotecaescolar.vista;

import com.bibliotecaescolar.controlador.AutenticacionControlador;
import com.bibliotecaescolar.dao.EstudianteDAO;
import com.bibliotecaescolar.dao.UsuarioDao;
import com.bibliotecaescolar.model.Estudiante;
import com.bibliotecaescolar.model.Rol;
import com.bibliotecaescolar.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class VentanaLogin extends JFrame {
    private final AutenticacionControlador controlador = new AutenticacionControlador(new UsuarioDao(), new EstudianteDAO());

    private JTextField campoUsuario;
    private JPasswordField campoContrasena;

    public VentanaLogin() {
        setTitle("Biblioteca Escolar - Inicio de Sesión");
        setSize(380, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel titulo = new JLabel("Sistema de Gestión de Biblioteca", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        add(titulo, BorderLayout.NORTH);

        JPanel panelFormulario = new JPanel(new GridLayout(2, 2, 5, 10));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        panelFormulario.add(new JLabel("RUT o correo:"));
        campoUsuario = new JTextField();
        panelFormulario.add(campoUsuario);

        panelFormulario.add(new JLabel("Contraseña:"));
        campoContrasena = new JPasswordField();
        panelFormulario.add(campoContrasena);

        add(panelFormulario, BorderLayout.CENTER);

        JButton botonIngresar = new JButton("Ingresar");
        botonIngresar.addActionListener(e -> iniciarSesion());
        getRootPane().setDefaultButton(botonIngresar);

        JPanel panelBoton = new JPanel();
        panelBoton.add(botonIngresar);
        add(panelBoton, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void iniciarSesion() {
        String usuarioIngresado = campoUsuario.getText().trim();
        String contrasena = new String(campoContrasena.getPassword());

        if (usuarioIngresado.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar RUT o correo y contraseña", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Usuario usuario = controlador.iniciarSesion(usuarioIngresado, contrasena);
            if (usuario == null) {
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos", "Acceso denegado", JOptionPane.ERROR_MESSAGE);
                campoContrasena.setText("");
                return;
            }

            Estudiante estudianteSesion = controlador.obtenerEstudianteDeUsuario(usuario);
            if (usuario.getRol() == Rol.ESTUDIANTE && estudianteSesion == null) {
                JOptionPane.showMessageDialog(this,
                        "Su usuario no está vinculado a un estudiante registrado.\nSolicite al bibliotecario que lo registre en \"Gestionar Estudiantes\".",
                        "Acceso denegado",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            new VentanaPrincipal(usuario, estudianteSesion);
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo validar el usuario:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
