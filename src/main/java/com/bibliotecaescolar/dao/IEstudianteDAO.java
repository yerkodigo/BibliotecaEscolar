package com.bibliotecaescolar.dao;

import com.bibliotecaescolar.model.Estudiante;

import java.sql.SQLException;

public interface IEstudianteDAO extends ICrudDAO<Estudiante> {
    public Estudiante readByRut(String rut) throws SQLException;
}
