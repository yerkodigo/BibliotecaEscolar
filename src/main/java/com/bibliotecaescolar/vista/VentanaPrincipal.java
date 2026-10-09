package com.bibliotecaescolar.vista;

import com.bibliotecaescolar.controlador.EstudianteControlador;
import com.bibliotecaescolar.controlador.LibroControlador;
import com.bibliotecaescolar.controlador.PrestamoControlador;
import com.bibliotecaescolar.controlador.ReporteControlador;
import com.bibliotecaescolar.controlador.UsuarioControlador;
import com.bibliotecaescolar.dao.CategoriaDAO;
import com.bibliotecaescolar.dao.EstudianteDAO;
import com.bibliotecaescolar.dao.ICategoriaDAO;
import com.bibliotecaescolar.dao.IEstudianteDAO;
import com.bibliotecaescolar.dao.ILibroDAO;
import com.bibliotecaescolar.dao.IPrestamoDAO;
import com.bibliotecaescolar.dao.IReporteDAO;
import com.bibliotecaescolar.dao.IUsuarioDAO;
import com.bibliotecaescolar.dao.LibroDAO;
import com.bibliotecaescolar.dao.PrestamoDAO;
import com.bibliotecaescolar.dao.ReporteDAO;
import com.bibliotecaescolar.dao.UsuarioDao;
import com.bibliotecaescolar.model.Estudiante;
import com.bibliotecaescolar.model.Modulo;
import com.bibliotecaescolar.model.Usuario;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {
    private final IUsuarioDAO usuarioDAO = new UsuarioDao();
    private final IEstudianteDAO estudianteDAO = new EstudianteDAO();
    private final ILibroDAO libroDAO = new LibroDAO();
    private final ICategoriaDAO categoriaDAO = new CategoriaDAO();
    private final IPrestamoDAO prestamoDAO = new PrestamoDAO();
    private final IReporteDAO reporteDAO = new ReporteDAO(prestamoDAO);

    private final LibroControlador libroControlador = new LibroControlador(libroDAO, categoriaDAO);
    private final EstudianteControlador estudianteControlador = new EstudianteControlador(estudianteDAO, usuarioDAO);
    private final UsuarioControlador usuarioControlador = new UsuarioControlador(usuarioDAO);
    private final PrestamoControlador prestamoControlador = new PrestamoControlador(prestamoDAO, libroDAO);
    private final ReporteControlador reporteControlador = new ReporteControlador(reporteDAO);

    private final Usuario usuario;

    public VentanaPrincipal(Usuario usuario, Estudiante estudianteSesion) {
        this.usuario = usuario;

        setTitle("Biblioteca Escolar - Menú Principal");
        setSize(420, 460);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel panelTitulo = new JPanel(new GridLayout(2, 1));
        JLabel titulo = new JLabel("Panel de Gestión Biblioteca Escolar", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        JLabel bienvenida = new JLabel("Bienvenido(a), " + usuario.getNombre() + " - " + usuario.getDescripcion(), SwingConstants.CENTER);
        panelTitulo.add(titulo);
        panelTitulo.add(bienvenida);
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        add(panelTitulo, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new GridLayout(7, 1, 10, 10));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40));

        JButton botonLibros = new JButton(usuario.tieneAcceso(Modulo.LIBROS) ? "Gestionar Libros" : "Consultar Catálogo de Libros");
        JButton botonEstudiantes = new JButton("Gestionar Estudiantes");
        JButton botonUsuarios = new JButton("Gestionar Usuarios");
        JButton botonPrestamos = new JButton("Préstamos y Devoluciones");
        JButton botonReportes = new JButton("Reportes");
        JButton botonCerrarSesion = new JButton("Cerrar Sesión");

        botonLibros.setEnabled(usuario.tieneAcceso(Modulo.LIBROS) || usuario.tieneAcceso(Modulo.CONSULTA));
        botonEstudiantes.setEnabled(usuario.tieneAcceso(Modulo.ESTUDIANTES));
        botonUsuarios.setEnabled(usuario.tieneAcceso(Modulo.USUARIOS));
        botonPrestamos.setEnabled(usuario.tieneAcceso(Modulo.PRESTAMOS));
        botonReportes.setEnabled(usuario.tieneAcceso(Modulo.REPORTES));

        botonLibros.addActionListener(e -> new VentanaGestionLibros(libroControlador, !usuario.tieneAcceso(Modulo.LIBROS)));
        botonEstudiantes.addActionListener(e -> new VentanaGestionEstudiantes(estudianteControlador));
        botonUsuarios.addActionListener(e -> new VentanaGestionUsuarios(usuarioControlador));
        botonPrestamos.addActionListener(e -> new VentanaPrestamos(prestamoControlador, libroControlador, estudianteControlador, estudianteSesion));
        botonReportes.addActionListener(e -> new VentanaReportes(reporteControlador, estudianteControlador));
        botonCerrarSesion.addActionListener(e -> cerrarSesion());

        panelBotones.add(botonLibros);
        panelBotones.add(botonEstudiantes);
        panelBotones.add(botonUsuarios);
        panelBotones.add(botonPrestamos);
        panelBotones.add(botonReportes);
        panelBotones.add(new JLabel());
        panelBotones.add(botonCerrarSesion);

        add(panelBotones, BorderLayout.CENTER);

        setVisible(true);
    }

    private void cerrarSesion() {
        int respuesta = JOptionPane.showConfirmDialog(this, "¿Desea cerrar la sesión de " + usuario.getNombre() + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        for (Window ventana : Window.getWindows()) {
            if (ventana != this) {
                ventana.dispose();
            }
        }
        dispose();
        new VentanaLogin();
    }
}
