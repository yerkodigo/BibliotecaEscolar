package com.bibliotecaescolar.dao;

import com.bibliotecaescolar.model.Prestamo;

import java.sql.SQLException;
import java.util.List;

public interface IReporteDAO {
    public List<Object[]> librosMasPrestados() throws SQLException;
    public List<Prestamo> historialPorEstudiante(int idEstudiante) throws SQLException;
    public List<Prestamo> librosEnPrestamo() throws SQLException;
}
