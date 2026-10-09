package com.bibliotecaescolar.vista;

import com.bibliotecaescolar.controlador.EstudianteControlador;
import com.bibliotecaescolar.model.Estudiante;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

public class VentanaGestionEstudiantes extends JFrame {
    private final EstudianteControlador controlador;
    private final DefaultTableModel modeloTabla;
    private final JTable tabla;

    private JTextField campoNombre;
    private JTextField campoRut;
    private JTextField campoCurso;
    private JTextField campoCorreo;
    private JCheckBox checkCrearAcceso;
    private JPasswordField campoContrasena;

    private static final String[] COLUMNAS = {"ID", "Nombre", "RUT", "Curso", "Correo"};

    public VentanaGestionEstudiantes(EstudianteControlador controlador) {
        this.controlador = controlador;

        setTitle("Gestión de Estudiantes");
        setSize(700, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel panelFormulario = new JPanel(new GridLayout(6, 2, 5, 5));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del estudiante"));

        panelFormulario.add(new JLabel("Nombre:"));
        campoNombre = new JTextField();
        panelFormulario.add(campoNombre);

        panelFormulario.add(new JLabel("RUT (ej: 12345678-9):"));
        campoRut = new JTextField();
        panelFormulario.add(campoRut);

        panelFormulario.add(new JLabel("Curso:"));
        campoCurso = new JTextField();
        panelFormulario.add(campoCurso);

        panelFormulario.add(new JLabel("Correo:"));
        campoCorreo = new JTextField();
        panelFormulario.add(campoCorreo);

        checkCrearAcceso = new JCheckBox("Crear acceso al sistema (solo al agregar)");
        checkCrearAcceso.addActionListener(e -> campoContrasena.setEnabled(checkCrearAcceso.isSelected()));
        panelFormulario.add(checkCrearAcceso);
        panelFormulario.add(new JLabel());

        panelFormulario.add(new JLabel("Contraseña de acceso:"));
        campoContrasena = new JPasswordField();
        campoContrasena.setEnabled(false);
        panelFormulario.add(campoContrasena);

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
            List<Estudiante> estudiantes = controlador.listarEstudiantes();
            for (Estudiante estudiante : estudiantes) {
                modeloTabla.addRow(new Object[]{
                        estudiante.getId(),
                        estudiante.getNombre(),
                        estudiante.getRut(),
                        estudiante.getCurso(),
                        estudiante.getCorreo()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los estudiantes:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarFilaSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila >= 0) {
            campoNombre.setText((String) modeloTabla.getValueAt(fila, 1));
            campoRut.setText((String) modeloTabla.getValueAt(fila, 2));
            campoCurso.setText((String) modeloTabla.getValueAt(fila, 3));
            campoCorreo.setText((String) modeloTabla.getValueAt(fila, 4));
        }
    }

    private Estudiante leerFormulario() {
        String nombre = campoNombre.getText().trim();
        String rut = campoRut.getText().trim();
        String curso = campoCurso.getText().trim();
        String correo = campoCorreo.getText().trim();

        if (nombre.isEmpty() || rut.isEmpty() || curso.isEmpty() || correo.isEmpty()) {
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
        if (curso.length() > 20) {
            JOptionPane.showMessageDialog(this, "El curso no puede superar los 20 caracteres", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (!correo.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+") || correo.length() > 100) {
            JOptionPane.showMessageDialog(this, "Ingrese un correo válido (ej: nombre@correo.cl)", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        return new Estudiante(null, nombre, rut, curso, correo);
    }

    private void agregar() {
        Estudiante estudiante = leerFormulario();
        if (estudiante == null) {
            return;
        }

        String contrasena = new String(campoContrasena.getPassword());
        if (checkCrearAcceso.isSelected() && contrasena.length() < 4) {
            JOptionPane.showMessageDialog(this, "La contraseña de acceso debe tener al menos 4 caracteres", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int idGenerado;
            if (checkCrearAcceso.isSelected()) {
                idGenerado = controlador.registrarEstudianteConAcceso(estudiante, contrasena);
            } else {
                idGenerado = controlador.registrarEstudiante(estudiante);
            }

            String mensaje = "Estudiante registrado correctamente.\nID: " + idGenerado;
            if (checkCrearAcceso.isSelected()) {
                mensaje += "\nPuede ingresar al sistema con su RUT o correo.";
            }
            JOptionPane.showMessageDialog(this, mensaje, "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            refrescarTabla();
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Ya existe un estudiante registrado con el RUT " + estudiante.getRut(), "Operación no permitida", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo registrar el estudiante:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione en la tabla el estudiante que desea actualizar", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Estudiante estudiante = leerFormulario();
        if (estudiante == null) {
            return;
        }
        estudiante.setId((int) modeloTabla.getValueAt(fila, 0));

        try {
            controlador.modificarEstudiante(estudiante);
            JOptionPane.showMessageDialog(this, "Estudiante actualizado correctamente.", "Actualización exitosa", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            refrescarTabla();
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Ya existe otro estudiante registrado con el RUT " + estudiante.getRut(), "Operación no permitida", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo actualizar el estudiante:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione en la tabla el estudiante que desea eliminar", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = (int) modeloTabla.getValueAt(fila, 0);
        String nombre = (String) modeloTabla.getValueAt(fila, 1);
        int respuesta = JOptionPane.showConfirmDialog(this, "¿Eliminar al estudiante " + nombre + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            controlador.eliminarEstudiante(id);
            JOptionPane.showMessageDialog(this, "Estudiante eliminado correctamente.", "Eliminación exitosa", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            refrescarTabla();
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "No se puede eliminar: el estudiante tiene préstamos registrados en su historial.", "Operación no permitida", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo eliminar el estudiante:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        campoNombre.setText("");
        campoRut.setText("");
        campoCurso.setText("");
        campoCorreo.setText("");
        campoContrasena.setText("");
        checkCrearAcceso.setSelected(false);
        campoContrasena.setEnabled(false);
        tabla.clearSelection();
    }
}
