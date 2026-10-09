package com.bibliotecaescolar.dao;

import com.bibliotecaescolar.model.Usuario;

import java.sql.SQLException;

public interface IUsuarioDAO extends ICrudDAO<Usuario> {
    public Usuario autenticar(String rutOCorreo, String contrasena) throws SQLException;
    public Usuario readByRut(String rut) throws SQLException;
}
