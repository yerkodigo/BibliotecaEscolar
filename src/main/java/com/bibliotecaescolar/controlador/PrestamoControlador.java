package com.bibliotecaescolar.controlador;

import com.bibliotecaescolar.dao.ILibroDAO;
import com.bibliotecaescolar.dao.IPrestamoDAO;
import com.bibliotecaescolar.model.Libro;
import com.bibliotecaescolar.model.Prestamo;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PrestamoControlador {
    private final IPrestamoDAO prestamoDAO;
    private final ILibroDAO libroDAO;

    public PrestamoControlador(IPrestamoDAO prestamoDAO, ILibroDAO libroDAO) {
        this.prestamoDAO = prestamoDAO;
        this.libroDAO = libroDAO;
    }

    public synchronized Prestamo registrarPrestamo(int idEstudiante, int idLibro) throws SQLException {
        String hilo = Thread.currentThread().getName();
        Libro libro = libroDAO.readById(idLibro);

        if (libro == null) {
            throw new SQLException("El libro seleccionado no existe.");
        }

        System.out.println("[" + hilo + "] Verificando stock de \"" + libro.getTitulo() + "\": " + libro.getStock());
        if (!libro.hayDisponibilidad()) {
            throw new SQLException("El libro \"" + libro.getTitulo() + "\" no tiene stock disponible.");
        }

        Prestamo prestamo = new Prestamo(idEstudiante, idLibro, LocalDate.now());
        int idPrestamo = prestamoDAO.registrarPrestamo(prestamo);
        prestamo.setId(idPrestamo);
        prestamo.setTituloLibro(libro.getTitulo());
        libro.descontarStock();

        System.out.println("[" + hilo + "] Préstamo #" + idPrestamo + " registrado. Stock restante de \""
                + libro.getTitulo() + "\": " + libro.getStock());
        return prestamo;
    }

    public synchronized long registrarDevolucion(Prestamo prestamo) throws SQLException {
        prestamoDAO.registrarDevolucion(prestamo);
        long diasAtraso = prestamo.calcularDiasAtraso();
        prestamo.setDevuelto(true);

        if (diasAtraso > 0) {
            System.out.println("[Devolución con atraso] Préstamo #" + prestamo.getId() + " - "
                    + prestamo.getNombreEstudiante() + " devolvió \"" + prestamo.getTituloLibro()
                    + "\" con " + diasAtraso + " días de atraso (vencía el " + prestamo.getFechaDevolucion() + ").");
        } else {
            System.out.println("[Devolución] Préstamo #" + prestamo.getId() + " devuelto a tiempo.");
        }
        return diasAtraso;
    }

    public List<Prestamo> listarPrestamosActivos() throws SQLException {
        return prestamoDAO.readActivos();
    }

    public List<Prestamo> listarPrestamosPorEstudiante(int idEstudiante) throws SQLException {
        return prestamoDAO.readByEstudiante(idEstudiante);
    }
}
