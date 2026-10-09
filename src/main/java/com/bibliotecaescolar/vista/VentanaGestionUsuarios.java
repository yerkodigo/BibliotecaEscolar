package com.bibliotecaescolar.vista;

import com.bibliotecaescolar.controlador.UsuarioControlador;
import com.bibliotecaescolar.model.Bibliotecario;
import com.bibliotecaescolar.model.Rol;
import com.bibliotecaescolar.model.Usuario;
import com.bibliotecaescolar.model.UsuarioEstudiante;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

public class VentanaGestionUsuarios extends JFrame {
    private final UsuarioControlador controlador;
    private final DefaultTableModel modeloTabla;
    private final JTable tabla;
    private List<Usuario> usuariosCargados = new ArrayList<>();

    private JTextField campoNombre;
    private JTextField campoRut;
    private JTextField campoCorreo;
    private JPasswordField campoContrasena;
    private JComboBox<Rol> comboRol;

    private static final String[] COLUMNAS = {"ID", "Nombre", "RUT", "Correo", "Rol"};

    public VentanaGestionUsuarios(UsuarioControlador controlador) {
        this.controlador = controlador;

        setTitle("Gestión de Usuarios");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel panelFormulario = new JPanel(new GridLayout(5, 2, 5, 5));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del usuario"));

        panelFormulario.add(new JLabel("Nombre:"));
        campoNombre = new JTextField();
        panelFormulario.add(campoNombre);

        panelFormulario.add(new JLabel("RUT (ej: 12345678-9):"));
        campoRut = new JTextField();
        panelFormulario.add(campoRut);

        panelFormulario.add(new JLabel("Correo:"));
        campoCorreo = new JTextField();
        panelFormulario.add(campoCorreo);

        panelFormulario.add(new JLabel("Contraseña:"));
        campoContrasena = new JPasswordField();
        panelFormulario.add(campoContrasena);

        panelFormulario.add(new JLabel("Rol:"));
        comboRol = new JComboBox<>(Rol.values());
        panelFormulario.add(comboRol);

        add(panelFormulario, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.getSelectionModel().addListSelectionListener(e -> cargarFilaSeleccionada());
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton botonAgregar = new JButton("Agregar");
        JButton botonActualizar = new JButton("Actualizar");
        JButton botonEliminar = new JButton("Eliminar");
        JButton botonLimpiar = new JButton("Limpiar");
        JButton botonRefrescar = new JButton("Refrescar");

        botonAgregar.addActionListener(e -> agregar());
        botonActualizar.addActionListener(e -> actualizar());
        botonEliminar.addActionListener(e -> eliminar());
        botonLimpiar.addActionListener(e -> limpiarFormulario());
        botonRefrescar.addActionListener(e -> refrescarTabla());

        JPanel panelBotones = new JPanel();
        panelBotones.add(botonAgregar);
        panelBotones.add(botonActualizar);
        panelBotones.add(botonEliminar);
        panelBotones.add(botonLimpiar);
        panelBotones.add(botonRefrescar);
        add(panelBotones, BorderLayout.SOUTH);

        refrescarTabla();
        setVisible(true);
    }

    public void refrescarTabla() {
        modeloTabla.setRowCount(0);
        try {
            usuariosCargados = controlador.listarUsuarios();
            for (Usuario usuario : usuariosCargados) {
                modeloTabla.addRow(new Object[]{
                        usuario.getId(),
                        usuario.getNombre(),
                        usuario.getRut(),
                        usuario.getCorreo(),
                        usuario.getDescripcion()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los usuarios:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarFilaSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila >= 0) {
            Usuario usuario = usuariosCargados.get(fila);
            campoNombre.setText(usuario.getNombre());
            campoRut.setText(usuario.getRut());
            campoCorreo.setText(usuario.getCorreo());
            campoContrasena.setText(usuario.getContrasena());
            comboRol.setSelectedItem(usuario.getRol());
        }
    }

    private Usuario leerFormulario() {
        String nombre = campoNombre.getText().trim();
        String rut = campoRut.getText().trim();
        String correo = campoCorreo.getText().trim();
        String contrasena = new String(campoContrasena.getPassword());
        Rol rol = (Rol) comboRol.getSelectedItem();

        if (nombre.isEmpty() || rut.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (nombre.length() > 100) {
            JOptionPane.showMessageDialog(this, "El nombre no puede superar los 100 caracteres", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (!rut.matches("\\d{7,8}-[\\dkK]")) {
            JOptionPane.showMessageDialog(this, "El RUT debe tener el formato 12345678-9 (sin puntos)", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (!correo.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+") || correo.length() > 100) {
            JOptionPane.showMessageDialog(this, "Ingrese un correo válido (ej: nombre@correo.cl)", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (contrasena.length() < 4 || contrasena.length() > 100) {
            JOptionPane.showMessageDialog(this, "La contraseña debe tener entre 4 y 100 caracteres", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        Usuario usuario;
        switch (rol) {
            case BIBLIOTECARIO -> usuario = new Bibliotecario(null, nombre, rut, correo, contrasena);
            default -> usuario = new UsuarioEstudiante(null, nombre, rut, correo, contrasena);
        }
        return usuario;
    }

    private void agregar() {
        Usuario usuario = leerFormulario();
        if (usuario == null) {
            return;
        }

        try {
            int idGenerado = controlador.registrarUsuario(usuario);
            String mensaje = "Usuario registrado correctamente.\nID: " + idGenerado + "\nRol: " + usuario.getDescripcion();
            if (usuario.getRol() == Rol.ESTUDIANTE) {
                mensaje += "\n\nRecuerde que debe existir un estudiante con el mismo RUT en \"Gestionar Estudiantes\".";
            }
            JOptionPane.showMessageDialog(this, mensaje, "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            refrescarTabla();
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Ya existe un usuario registrado con el RUT " + usuario.getRut(), "Operación no permitida", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo registrar el usuario:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione en la tabla el usuario que desea actualizar", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Usuario usuario = leerFormulario();
        if (usuario == null) {
            return;
        }
        usuario.setId((int) modeloTabla.getValueAt(fila, 0));

        try {
            controlador.modificarUsuario(usuario);
            JOptionPane.showMessageDialog(this, "Usuario actualizado correctamente.", "Actualización exitosa", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            refrescarTabla();
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Ya existe otro usuario registrado con el RUT " + usuario.getRut(), "Operación no permitida", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo actualizar el usuario:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione en la tabla el usuario que desea eliminar", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = (int) modeloTabla.getValueAt(fila, 0);
        String nombre = (String) modeloTabla.getValueAt(fila, 1);
        int respuesta = JOptionPane.showConfirmDialog(this, "¿Eliminar al usuario " + nombre + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            controlador.eliminarUsuario(id);
            JOptionPane.showMessageDialog(this, "Usuario eliminado correctamente.", "Eliminación exitosa", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            refrescarTabla();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo eliminar el usuario:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        campoNombre.setText("");
        campoRut.setText("");
        campoCorreo.setText("");
        campoContrasena.setText("");
        comboRol.setSelectedIndex(0);
        tabla.clearSelection();
    }
}
