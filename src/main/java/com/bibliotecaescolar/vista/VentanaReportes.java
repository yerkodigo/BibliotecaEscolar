package com.bibliotecaescolar.vista;

import com.bibliotecaescolar.controlador.EstudianteControlador;
import com.bibliotecaescolar.controlador.ReporteControlador;
import com.bibliotecaescolar.model.Estudiante;
import com.bibliotecaescolar.model.Prestamo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VentanaReportes extends JFrame {
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final ReporteControlador reporteControlador;
    private final EstudianteControlador estudianteControlador;

    private final DefaultTableModel modeloMasPrestados;
    private final DefaultTableModel modeloHistorial;
    private final DefaultTableModel modeloEnPrestamo;
    private JComboBox<Estudiante> comboEstudiantes;

    public VentanaReportes(ReporteControlador reporteControlador, EstudianteControlador estudianteControlador) {
        this.reporteControlador = reporteControlador;
        this.estudianteControlador = estudianteControlador;

        setTitle("Reportes de Biblioteca");
        setSize(800, 480);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        modeloMasPrestados = crearModelo(new String[]{"Posición", "Título", "Autor", "Veces prestado"});
        modeloHistorial = crearModelo(new String[]{"ID", "Libro", "Fecha préstamo", "Fecha vencimiento", "Estado"});
        modeloEnPrestamo = crearModelo(new String[]{"ID", "Libro", "Estudiante", "Fecha préstamo", "Fecha vencimiento", "Estado"});

        JPanel panelHistorial = new JPanel(new BorderLayout(5, 5));
        JPanel panelSeleccion = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSeleccion.add(new JLabel("Estudiante:"));
        comboEstudiantes = new JComboBox<>();
        comboEstudiantes.addActionListener(e -> cargarHistorial());
        panelSeleccion.add(comboEstudiantes);
        panelHistorial.add(panelSeleccion, BorderLayout.NORTH);
        panelHistorial.add(new JScrollPane(new JTable(modeloHistorial)), BorderLayout.CENTER);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Libros más prestados", new JScrollPane(new JTable(modeloMasPrestados)));
        pestanas.addTab("Historial por estudiante", panelHistorial);
        pestanas.addTab("Libros en préstamo", new JScrollPane(new JTable(modeloEnPrestamo)));
        add(pestanas, BorderLayout.CENTER);

        JButton botonRefrescar = new JButton("Refrescar");
        botonRefrescar.addActionListener(e -> cargarReportes());
        JPanel panelBotones = new JPanel();
        panelBotones.add(botonRefrescar);
        add(panelBotones, BorderLayout.SOUTH);

        cargarEstudiantes();
        cargarReportes();
        setVisible(true);
    }

    private DefaultTableModel crearModelo(String[] columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void cargarEstudiantes() {
        try {
            for (Estudiante estudiante : estudianteControlador.listarEstudiantes()) {
                comboEstudiantes.addItem(estudiante);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los estudiantes:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarReportes() {
        cargarLibrosMasPrestados();
        cargarHistorial();
        cargarLibrosEnPrestamo();
    }

    private void cargarLibrosMasPrestados() {
        modeloMasPrestados.setRowCount(0);
        try {
            List<Object[]> filas = reporteControlador.obtenerLibrosMasPrestados();
            int posicion = 1;
            for (Object[] fila : filas) {
                modeloMasPrestados.addRow(new Object[]{posicion, fila[0], fila[1], fila[2]});
                posicion++;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo generar el reporte de libros más prestados:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarHistorial() {
        modeloHistorial.setRowCount(0);
        Estudiante estudiante = (Estudiante) comboEstudiantes.getSelectedItem();
        if (estudiante == null) {
            return;
        }

        try {
            for (Prestamo prestamo : reporteControlador.obtenerHistorialEstudiante(estudiante.getId())) {
                modeloHistorial.addRow(new Object[]{
                        prestamo.getId(),
                        prestamo.getTituloLibro(),
                        prestamo.getFechaPrestamo().format(FORMATO_FECHA),
                        prestamo.getFechaDevolucion().format(FORMATO_FECHA),
                        prestamo.getEstadoTexto()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo generar el historial del estudiante:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarLibrosEnPrestamo() {
        modeloEnPrestamo.setRowCount(0);
        try {
            for (Prestamo prestamo : reporteControlador.obtenerLibrosEnPrestamo()) {
                modeloEnPrestamo.addRow(new Object[]{
                        prestamo.getId(),
                        prestamo.getTituloLibro(),
                        prestamo.getNombreEstudiante(),
                        prestamo.getFechaPrestamo().format(FORMATO_FECHA),
                        prestamo.getFechaDevolucion().format(FORMATO_FECHA),
                        prestamo.getEstadoTexto()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo generar el reporte de libros en préstamo:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
