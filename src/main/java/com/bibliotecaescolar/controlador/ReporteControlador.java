package com.bibliotecaescolar.controlador;

import com.bibliotecaescolar.dao.IReporteDAO;
import com.bibliotecaescolar.model.Prestamo;

import java.sql.SQLException;
import java.util.List;

public class ReporteControlador {
    private final IReporteDAO reporteDAO;

    public ReporteControlador(IReporteDAO reporteDAO) {
        this.reporteDAO = reporteDAO;
    }

    public List<Object[]> obtenerLibrosMasPrestados() throws SQLException {
        return reporteDAO.librosMasPrestados();
    }

    public List<Prestamo> obtenerHistorialEstudiante(int idEstudiante) throws SQLException {
        return reporteDAO.historialPorEstudiante(idEstudiante);
    }

    public List<Prestamo> obtenerLibrosEnPrestamo() throws SQLException {
        return reporteDAO.librosEnPrestamo();
    }
}
