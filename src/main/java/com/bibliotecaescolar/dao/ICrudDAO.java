package com.bibliotecaescolar.dao;

import java.sql.SQLException;
import java.util.List;

//con la T puedo luego ocupar cualquier clase para reemplazar y asi es una interfaz crud mas reutilizable
public interface ICrudDAO<T> {
    public int create(T entidad) throws SQLException;

    public List<T> readAll() throws SQLException;
    public T readById(int id) throws SQLException;

    public void update(T entidad) throws SQLException;

    public void delete(int id) throws SQLException;
}
