package com.bibliotecaescolar.vista;

import com.bibliotecaescolar.concurrencia.PrestamoWorker;
import com.bibliotecaescolar.controlador.EstudianteControlador;
import com.bibliotecaescolar.controlador.LibroControlador;
import com.bibliotecaescolar.controlador.PrestamoControlador;
import com.bibliotecaescolar.model.Estudiante;
import com.bibliotecaescolar.model.Libro;
import com.bibliotecaescolar.model.Prestamo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VentanaPrestamos extends JFrame {
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final String[] COLUMNAS = {"ID", "Estudiante", "Libro", "Fecha préstamo", "Fecha vencimiento", "Estado"};

    private final PrestamoControlador prestamoControlador;
    private final LibroControlador libroControlador;
    private final EstudianteControlador estudianteControlador;
    private final Estudiante estudianteSesion;

    private final DefaultTableModel modeloTabla;
    private final JTable tabla;
    private List<Prestamo> prestamosCargados = new ArrayList<>();

    private JComboBox<Estudiante> comboEstudiantes;
    private JComboBox<Libro> comboLibros;
    private JLabel etiquetaEstado;
    private JProgressBar barraProgreso;
    private int prestamosEnProceso = 0;

    public VentanaPrestamos(PrestamoControlador prestamoControlador, LibroControlador libroControlador,
                            EstudianteControlador estudianteControlador, Estudiante estudianteSesion) {
        this.prestamoControlador = prestamoControlador;
        this.libroControlador = libroControlador;
        this.estudianteControlador = estudianteControlador;
        this.estudianteSesion = estudianteSesion;

        setTitle(estudianteSesion == null ? "Préstamos y Devoluciones" : "Mis Préstamos - " + estudianteSesion.getNombre());
        setSize(800, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel panelFormulario = new JPanel(new GridLayout(4, 2, 5, 5));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Nuevo préstamo (plazo: " + Prestamo.DIAS_PRESTAMO + " días)"));

        panelFormulario.add(new JLabel("Estudiante:"));
        comboEstudiantes = new JComboBox<>();
        panelFormulario.add(comboEstudiantes);

        panelFormulario.add(new JLabel("Libro disponible:"));
        comboLibros = new JComboBox<>();
        panelFormulario.add(comboLibros);

        JButton botonPrestar = new JButton("Registrar Préstamo");
        botonPrestar.addActionListener(e -> registrarPrestamo());
        panelFormulario.add(new JLabel());
        panelFormulario.add(botonPrestar);

        etiquetaEstado = new JLabel(" ");
        barraProgreso = new JProgressBar();
        barraProgreso.setIndeterminate(true);
        barraProgreso.setVisible(false);
        panelFormulario.add(etiquetaEstado);
        panelFormulario.add(barraProgreso);

        add(panelFormulario, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setBorder(BorderFactory.createTitledBorder(estudianteSesion == null ? "Préstamos activos" : "Mis préstamos"));
        add(scrollTabla, BorderLayout.CENTER);

        JButton botonDevolver = new JButton("Registrar Devolución");
        JButton botonRefrescar = new JButton("Refrescar");
        botonDevolver.addActionListener(e -> registrarDevolucion());
        botonRefrescar.addActionListener(e -> cargarDatos());

        JPanel panelBotones = new JPanel();
        panelBotones.add(botonDevolver);
        panelBotones.add(botonRefrescar);
        add(panelBotones, BorderLayout.SOUTH);

        cargarDatos();
        setVisible(true);
    }

    private void cargarDatos() {
        comboEstudiantes.removeAllItems();
        comboLibros.removeAllItems();
        modeloTabla.setRowCount(0);

        try {
            if (estudianteSesion != null) {
                comboEstudiantes.addItem(estudianteSesion);
                comboEstudiantes.setEnabled(false);
                prestamosCargados = prestamoControlador.listarPrestamosPorEstudiante(estudianteSesion.getId());
            } else {
                for (Estudiante estudiante : estudianteControlador.listarEstudiantes()) {
                    comboEstudiantes.addItem(estudiante);
                }
                prestamosCargados = prestamoControlador.listarPrestamosActivos();
            }

            for (Libro libro : libroControlador.listarLibrosDisponibles()) {
                comboLibros.addItem(libro);
            }

            for (Prestamo prestamo : prestamosCargados) {
                modeloTabla.addRow(new Object[]{
                        prestamo.getId(),
                        prestamo.getNombreEstudiante(),
                        prestamo.getTituloLibro(),
                        prestamo.getFechaPrestamo().format(FORMATO_FECHA),
                        prestamo.getFechaDevolucion().format(FORMATO_FECHA),
                        prestamo.getEstadoTexto()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los datos:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarPrestamo() {
        Estudiante estudiante = (Estudiante) comboEstudiantes.getSelectedItem();
        Libro libro = (Libro) comboLibros.getSelectedItem();

        if (estudiante == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un estudiante", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (libro == null) {
            JOptionPane.showMessageDialog(this, "No hay libros con stock disponible para prestar", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        prestamosEnProceso++;
        actualizarEstadoProceso();

        PrestamoWorker worker = new PrestamoWorker(prestamoControlador, estudiante, libro, this);
        worker.execute();
    }

    public void prestamoRegistrado(Prestamo prestamo) {
        prestamosEnProceso--;
        actualizarEstadoProceso();
        cargarDatos();

        JOptionPane.showMessageDialog(this,
                "Préstamo #" + prestamo.getId() + " registrado correctamente.\n" +
                        "Estudiante: " + prestamo.getNombreEstudiante() + "\n" +
                        "Libro: " + prestamo.getTituloLibro() + "\n" +
                        "Fecha de vencimiento: " + prestamo.getFechaDevolucion().format(FORMATO_FECHA),
                "Préstamo exitoso",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void prestamoFallido(String mensaje) {
        prestamosEnProceso--;
        actualizarEstadoProceso();
        cargarDatos();

        JOptionPane.showMessageDialog(this, "No se pudo registrar el préstamo:\n" + mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void actualizarEstadoProceso() {
        if (prestamosEnProceso > 0) {
            etiquetaEstado.setText("Procesando " + prestamosEnProceso + " préstamo(s)...");
            barraProgreso.setVisible(true);
        } else {
            etiquetaEstado.setText(" ");
            barraProgreso.setVisible(false);
        }
    }

    private void registrarDevolucion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione en la tabla el préstamo que desea devolver", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Prestamo prestamo = prestamosCargados.get(fila);
        if (prestamo.isDevuelto()) {
            JOptionPane.showMessageDialog(this, "El préstamo #" + prestamo.getId() + " ya fue devuelto", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Registrar la devolución de \"" + prestamo.getTituloLibro() + "\" (" + prestamo.getNombreEstudiante() + ")?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            long diasAtraso = prestamoControlador.registrarDevolucion(prestamo);
            if (diasAtraso > 0) {
                JOptionPane.showMessageDialog(this,
                        "Devolución registrada CON ATRASO.\n" +
                                "Fecha de vencimiento: " + prestamo.getFechaDevolucion().format(FORMATO_FECHA) + "\n" +
                                "Días de atraso: " + diasAtraso,
                        "Devolución con atraso",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Devolución registrada correctamente dentro del plazo.", "Devolución exitosa", JOptionPane.INFORMATION_MESSAGE);
            }
            cargarDatos();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo registrar la devolución:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
