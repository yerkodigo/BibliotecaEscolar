package com.bibliotecaescolar.main;

import com.bibliotecaescolar.config.DatabaseConnection;
import com.bibliotecaescolar.controlador.PrestamoControlador;
import com.bibliotecaescolar.dao.ILibroDAO;
import com.bibliotecaescolar.dao.LibroDAO;
import com.bibliotecaescolar.dao.PrestamoDAO;
import com.bibliotecaescolar.model.Libro;
import com.bibliotecaescolar.model.Prestamo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PruebaConcurrenciaPrestamos {
    private static final int ID_LIBRO = 3;
    private static final int[] ID_ESTUDIANTES = {1, 2, 3, 4, 5};

    private static int prestamosExitosos = 0;
    private static int prestamosRechazados = 0;

    private static synchronized void contarResultado(boolean exitoso) {
        if (exitoso) {
            prestamosExitosos++;
        } else {
            prestamosRechazados++;
        }
    }

    public static void main(String[] args) {
        ILibroDAO libroDAO = new LibroDAO();
        PrestamoControlador controlador = new PrestamoControlador(new PrestamoDAO(), libroDAO);

        try {
            Libro libro = libroDAO.readById(ID_LIBRO);
            if (libro == null) {
                System.out.println("No existe el libro con id " + ID_LIBRO);
                return;
            }

            System.out.println("=== Prueba de préstamos simultáneos ===");
            System.out.println("Libro: \"" + libro.getTitulo() + "\" - stock inicial: " + libro.getStock());
            System.out.println("Solicitudes simultáneas: " + ID_ESTUDIANTES.length);
            System.out.println();

            List<Thread> hilos = new ArrayList<>();
            for (int idEstudiante : ID_ESTUDIANTES) {
                Thread hilo = new Thread(() -> {
                    try {
                        Prestamo prestamo = controlador.registrarPrestamo(idEstudiante, ID_LIBRO);
                        System.out.println("[" + Thread.currentThread().getName() + "] OK -> préstamo #" + prestamo.getId());
                        contarResultado(true);
                    } catch (SQLException e) {
                        System.out.println("[" + Thread.currentThread().getName() + "] RECHAZADO -> " + e.getMessage());
                        contarResultado(false);
                    }
                }, "Estudiante-" + idEstudiante);
                hilos.add(hilo);
            }

            for (Thread hilo : hilos) {
                hilo.start();
            }
            for (Thread hilo : hilos) {
                hilo.join();
            }

            Libro libroFinal = libroDAO.readById(ID_LIBRO);
            System.out.println();
            System.out.println("Préstamos exitosos: " + prestamosExitosos);
            System.out.println("Préstamos rechazados: " + prestamosRechazados);
            System.out.println("Stock final: " + libroFinal.getStock());
            System.out.println(libroFinal.getStock() >= 0 && prestamosExitosos == libro.getStock()
                    ? "Resultado: el stock se mantuvo consistente."
                    : "Resultado: se detectó una inconsistencia en el stock.");
        } catch (SQLException e) {
            System.err.println("Error de base de datos: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("La prueba fue interrumpida.");
        } finally {
            try {
                DatabaseConnection.getInstance().cerrarConexion();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}
