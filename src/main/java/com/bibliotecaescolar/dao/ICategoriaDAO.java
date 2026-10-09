package com.bibliotecaescolar.dao;

import com.bibliotecaescolar.model.Categoria;

import java.sql.SQLException;
import java.util.List;

public interface ICategoriaDAO {
    public List<Categoria> readAll() throws SQLException;
}
