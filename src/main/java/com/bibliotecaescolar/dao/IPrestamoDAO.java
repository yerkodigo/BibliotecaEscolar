package com.bibliotecaescolar.dao;

import com.bibliotecaescolar.model.Prestamo;

import java.sql.SQLException;
import java.util.List;

public interface IPrestamoDAO {
    public int registrarPrestamo(Prestamo prestamo) throws SQLException;

    public List<Prestamo> readAll() throws SQLException;
    public List<Prestamo> readActivos() throws SQLException;
    public List<Prestamo> readByEstudiante(int idEstudiante) throws SQLException;
    public Prestamo readById(int id) throws SQLException;

    public void registrarDevolucion(Prestamo prestamo) throws SQLException;
}
