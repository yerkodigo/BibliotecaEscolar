package com.bibliotecaescolar.vista;

import com.bibliotecaescolar.controlador.LibroControlador;
import com.bibliotecaescolar.model.Categoria;
import com.bibliotecaescolar.model.Libro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

public class VentanaGestionLibros extends JFrame {
    private final LibroControlador controlador;
    private final boolean soloLectura;
    private final DefaultTableModel modeloTabla;
    private final JTable tabla;

    private JTextField campoTitulo;
    private JTextField campoAutor;
    private JTextField campoIsbn;
    private JTextField campoEditorial;
    private JTextField campoStock;
    private JComboBox<Categoria> comboCategoria;

    private JTextField campoBuscarTitulo;
    private JComboBox<Categoria> comboFiltroCategoria;

    private static final String[] COLUMNAS = {"ID", "Título", "Autor", "ISBN", "Editorial", "Stock", "Categoría"};

    public VentanaGestionLibros(LibroControlador controlador, boolean soloLectura) {
        this.controlador = controlador;
        this.soloLectura = soloLectura;

        setTitle(soloLectura ? "Catálogo de Libros" : "Gestión de Libros");
        setSize(850, 560);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel panelFormulario = new JPanel(new GridLayout(6, 2, 5, 5));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del libro"));

        panelFormulario.add(new JLabel("Título:"));
        campoTitulo = new JTextField();
        panelFormulario.add(campoTitulo);

        panelFormulario.add(new JLabel("Autor:"));
        campoAutor = new JTextField();
        panelFormulario.add(campoAutor);

        panelFormulario.add(new JLabel("ISBN:"));
        campoIsbn = new JTextField();
        panelFormulario.add(campoIsbn);

        panelFormulario.add(new JLabel("Editorial:"));
        campoEditorial = new JTextField();
        panelFormulario.add(campoEditorial);

        panelFormulario.add(new JLabel("Stock:"));
        campoStock = new JTextField();
        panelFormulario.add(campoStock);

        panelFormulario.add(new JLabel("Categoría:"));
        comboCategoria = new JComboBox<>();
        panelFormulario.add(comboCategoria);

        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFiltro.setBorder(BorderFactory.createTitledBorder("Buscar"));
        panelFiltro.add(new JLabel("Título:"));
        campoBuscarTitulo = new JTextField(15);
        panelFiltro.add(campoBuscarTitulo);
        panelFiltro.add(new JLabel("Categoría:"));
        comboFiltroCategoria = new JComboBox<>();
        panelFiltro.add(comboFiltroCategoria);
        JButton botonBuscar = new JButton("Buscar");
        botonBuscar.addActionListener(e -> buscar());
        panelFiltro.add(botonBuscar);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        if (!soloLectura) {
            panelSuperior.add(panelFormulario, BorderLayout.CENTER);
        }
        panelSuperior.add(panelFiltro, BorderLayout.SOUTH);
        add(panelSuperior, BorderLayout.NORTH);

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
        if (!soloLectura) {
            panelBotones.add(botonAgregar);
            panelBotones.add(botonActualizar);
            panelBotones.add(botonEliminar);
            panelBotones.add(botonLimpiar);
        }
        panelBotones.add(botonRefrescar);
        add(panelBotones, BorderLayout.SOUTH);

        cargarCategorias();
        refrescarTabla();
        setVisible(true);
    }

    private void cargarCategorias() {
        comboCategoria.removeAllItems();
        comboFiltroCategoria.removeAllItems();
        comboFiltroCategoria.addItem(new Categoria(null, "Todas"));
        try {
            for (Categoria categoria : controlador.listarCategorias()) {
                comboCategoria.addItem(categoria);
                comboFiltroCategoria.addItem(categoria);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar las categorías:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void llenarTabla(List<Libro> libros) {
        modeloTabla.setRowCount(0);
        for (Libro libro : libros) {
            modeloTabla.addRow(new Object[]{
                    libro.getId(),
                    libro.getTitulo(),
                    libro.getAutor(),
                    libro.getIsbn(),
                    libro.getEditorial(),
                    libro.getStock(),
                    libro.getNombreCategoria()
            });
        }
    }

    public void refrescarTabla() {
        try {
            llenarTabla(controlador.listarLibros());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los libros:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscar() {
        String titulo = campoBuscarTitulo.getText().trim();
        Categoria categoria = (Categoria) comboFiltroCategoria.getSelectedItem();

        String filtroTitulo = titulo.isEmpty() ? null : titulo;
        Integer filtroCategoria = categoria == null ? null : categoria.getId();

        try {
            llenarTabla(controlador.buscarLibros(filtroTitulo, filtroCategoria));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo realizar la búsqueda:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarFilaSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila < 0 || soloLectura) {
            return;
        }

        campoTitulo.setText((String) modeloTabla.getValueAt(fila, 1));
        campoAutor.setText((String) modeloTabla.getValueAt(fila, 2));
        campoIsbn.setText((String) modeloTabla.getValueAt(fila, 3));
        campoEditorial.setText((String) modeloTabla.getValueAt(fila, 4));
        campoStock.setText(String.valueOf(modeloTabla.getValueAt(fila, 5)));

        String nombreCategoria = (String) modeloTabla.getValueAt(fila, 6);
        for (int i = 0; i < comboCategoria.getItemCount(); i++) {
            if (comboCategoria.getItemAt(i).getNombre().equals(nombreCategoria)) {
                comboCategoria.setSelectedIndex(i);
                break;
            }
        }
    }

    private Libro leerFormulario() {
        String titulo = campoTitulo.getText().trim();
        String autor = campoAutor.getText().trim();
        String isbn = campoIsbn.getText().trim();
        String editorial = campoEditorial.getText().trim();
        String textoStock = campoStock.getText().trim();
        Categoria categoria = (Categoria) comboCategoria.getSelectedItem();

        if (titulo.isEmpty() || autor.isEmpty() || isbn.isEmpty() || editorial.isEmpty() || textoStock.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (titulo.length() > 200) {
            JOptionPane.showMessageDialog(this, "El título no puede superar los 200 caracteres", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (autor.length() > 100 || editorial.length() > 100) {
            JOptionPane.showMessageDialog(this, "El autor y la editorial no pueden superar los 100 caracteres", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (!isbn.matches("[0-9-]{10,20}")) {
            JOptionPane.showMessageDialog(this, "El ISBN debe tener entre 10 y 20 caracteres y solo números o guiones", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        int stock;
        try {
            stock = Integer.parseInt(textoStock);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El stock debe ser un número entero", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (stock < 0) {
            JOptionPane.showMessageDialog(this, "El stock no puede ser negativo", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (categoria == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una categoría", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        return new Libro(null, titulo, autor, isbn, editorial, stock, categoria.getId());
    }

    private void agregar() {
        Libro libro = leerFormulario();
        if (libro == null) {
            return;
        }

        try {
            int idGenerado = controlador.registrarLibro(libro);
            JOptionPane.showMessageDialog(this, "Libro registrado correctamente.\nID: " + idGenerado, "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            refrescarTabla();
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Ya existe un libro registrado con el ISBN " + libro.getIsbn(), "Operación no permitida", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo registrar el libro:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione en la tabla el libro que desea actualizar", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Libro libro = leerFormulario();
        if (libro == null) {
            return;
        }
        libro.setId((int) modeloTabla.getValueAt(fila, 0));

        try {
            controlador.modificarLibro(libro);
            JOptionPane.showMessageDialog(this, "Libro actualizado correctamente.", "Actualización exitosa", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            refrescarTabla();
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Ya existe otro libro registrado con el ISBN " + libro.getIsbn(), "Operación no permitida", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo actualizar el libro:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione en la tabla el libro que desea eliminar", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = (int) modeloTabla.getValueAt(fila, 0);
        String titulo = (String) modeloTabla.getValueAt(fila, 1);
        int respuesta = JOptionPane.showConfirmDialog(this, "¿Eliminar el libro \"" + titulo + "\"?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            controlador.eliminarLibro(id);
            JOptionPane.showMessageDialog(this, "Libro eliminado correctamente.", "Eliminación exitosa", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            refrescarTabla();
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "No se puede eliminar: el libro tiene préstamos registrados.", "Operación no permitida", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo eliminar el libro:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        campoTitulo.setText("");
        campoAutor.setText("");
        campoIsbn.setText("");
        campoEditorial.setText("");
        campoStock.setText("");
        if (comboCategoria.getItemCount() > 0) {
            comboCategoria.setSelectedIndex(0);
        }
        tabla.clearSelection();
    }
}
