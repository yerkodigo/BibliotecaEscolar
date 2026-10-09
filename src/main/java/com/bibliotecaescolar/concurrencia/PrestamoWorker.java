package com.bibliotecaescolar.concurrencia;

import com.bibliotecaescolar.controlador.PrestamoControlador;
import com.bibliotecaescolar.model.Estudiante;
import com.bibliotecaescolar.model.Libro;
import com.bibliotecaescolar.model.Prestamo;
import com.bibliotecaescolar.vista.VentanaPrestamos;

import javax.swing.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

public class PrestamoWorker extends SwingWorker<Prestamo, Void> {
    private final PrestamoControlador controlador;
    private final Estudiante estudiante;
    private final Libro libro;
    private final VentanaPrestamos ventana;

    public PrestamoWorker(PrestamoControlador controlador, Estudiante estudiante, Libro libro, VentanaPrestamos ventana) {
        this.controlador = controlador;
        this.estudiante = estudiante;
        this.libro = libro;
        this.ventana = ventana;
    }

    @Override
    protected Prestamo doInBackground() throws Exception {
        Thread.currentThread().setName("Prestamo-" + estudiante.getNombre());
        System.out.println("[" + Thread.currentThread().getName() + "] Procesando préstamo de \"" + libro.getTitulo() + "\"...");

        int tiempoProcesoMs = ThreadLocalRandom.current().nextInt(1000, 2501);
        Thread.sleep(tiempoProcesoMs);

        return controlador.registrarPrestamo(estudiante.getId(), libro.getId());
    }

    @Override
    protected void done() {
        try {
            Prestamo prestamo = get();
            prestamo.setNombreEstudiante(estudiante.getNombre());
            ventana.prestamoRegistrado(prestamo);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            ventana.prestamoFallido("El proceso del préstamo fue interrumpido.");
        } catch (ExecutionException e) {
            ventana.prestamoFallido(e.getCause().getMessage());
        }
    }
}
