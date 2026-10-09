package com.bibliotecaescolar.dao;

import com.bibliotecaescolar.model.Libro;

import java.sql.SQLException;
import java.util.List;

public interface ILibroDAO extends ICrudDAO<Libro> {
    public List<Libro> readByFiltro(String titulo, Integer idCategoria) throws SQLException;
    public List<Libro> readDisponibles() throws SQLException;
}
