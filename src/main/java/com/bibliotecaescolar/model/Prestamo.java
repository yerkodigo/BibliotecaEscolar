package com.bibliotecaescolar.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Prestamo {
    public static final int DIAS_PRESTAMO = 7;

    private Integer id;
    private Integer idEstudiante;
    private Integer idLibro;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
    private boolean devuelto;

    private String nombreEstudiante;
    private String tituloLibro;

    public Prestamo(Integer idEstudiante, Integer idLibro, LocalDate fechaPrestamo) {
        this.idEstudiante = idEstudiante;
        this.idLibro = idLibro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = calcularFechaVencimiento();
        this.devuelto = false;
    }

    public Prestamo(Integer id, Integer idEstudiante, Integer idLibro, LocalDate fechaPrestamo, LocalDate fechaDevolucion, boolean devuelto) {
        this.id = id;
        this.idEstudiante = idEstudiante;
        this.idLibro = idLibro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.devuelto = devuelto;
    }

    public Prestamo() {}

    public LocalDate calcularFechaVencimiento() {
        return fechaPrestamo.plusDays(DIAS_PRESTAMO);
    }

    public boolean estaAtrasado() {
        return !devuelto && LocalDate.now().isAfter(fechaDevolucion);
    }

    public long calcularDiasAtraso() {
        if (!LocalDate.now().isAfter(fechaDevolucion)) {
            return 0;
        }
//        para calcular los dias entre hoy y la de devolucion
        return ChronoUnit.DAYS.between(fechaDevolucion, LocalDate.now());
    }

    public String getEstadoTexto() {
        if (devuelto) {
            return "Devuelto";
        }
        if (estaAtrasado()) {
            return "Atrasado (" + calcularDiasAtraso() + " días)";
        }
        return "En préstamo";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(Integer idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public Integer getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(Integer idLibro) {
        this.idLibro = idLibro;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public boolean isDevuelto() {
        return devuelto;
    }

    public void setDevuelto(boolean devuelto) {
        this.devuelto = devuelto;
    }

    public String getNombreEstudiante() {
        return nombreEstudiante;
    }

    public void setNombreEstudiante(String nombreEstudiante) {
        this.nombreEstudiante = nombreEstudiante;
    }

    public String getTituloLibro() {
        return tituloLibro;
    }

    public void setTituloLibro(String tituloLibro) {
        this.tituloLibro = tituloLibro;
    }

    @Override
    public String toString() {
        return "#" + id + " - " + tituloLibro + " - " + nombreEstudiante;
    }
}
