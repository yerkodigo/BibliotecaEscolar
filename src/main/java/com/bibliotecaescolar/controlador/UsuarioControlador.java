package com.bibliotecaescolar.controlador;

import com.bibliotecaescolar.dao.IUsuarioDAO;
import com.bibliotecaescolar.model.Usuario;

import java.sql.SQLException;
import java.util.List;

public class UsuarioControlador {
    private final IUsuarioDAO usuarioDAO;

    public UsuarioControlador(IUsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public int registrarUsuario(Usuario usuario) throws SQLException {
        return usuarioDAO.create(usuario);
    }

    public List<Usuario> listarUsuarios() throws SQLException {
        return usuarioDAO.readAll();
    }

    public void modificarUsuario(Usuario usuario) throws SQLException {
        usuarioDAO.update(usuario);
    }

    public void eliminarUsuario(int id) throws SQLException {
        usuarioDAO.delete(id);
    }
}
