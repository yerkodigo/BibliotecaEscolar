package com.bibliotecaescolar.controlador;

import com.bibliotecaescolar.dao.IEstudianteDAO;
import com.bibliotecaescolar.dao.IUsuarioDAO;
import com.bibliotecaescolar.model.Estudiante;
import com.bibliotecaescolar.model.Rol;
import com.bibliotecaescolar.model.Usuario;

import java.sql.SQLException;

public class AutenticacionControlador {
    private final IUsuarioDAO usuarioDAO;
    private final IEstudianteDAO estudianteDAO;

    public AutenticacionControlador(IUsuarioDAO usuarioDAO, IEstudianteDAO estudianteDAO) {
        this.usuarioDAO = usuarioDAO;
        this.estudianteDAO = estudianteDAO;
    }

    public Usuario iniciarSesion(String rutOCorreo, String contrasena) throws SQLException {
        Usuario usuario = usuarioDAO.autenticar(rutOCorreo, contrasena);
        if (usuario != null) {
            System.out.println("Inicio de sesión: " + usuario.getNombre() + " (" + usuario.getDescripcion() + ")");
        }
        return usuario;
    }

    public Estudiante obtenerEstudianteDeUsuario(Usuario usuario) throws SQLException {
        if (usuario.getRol() != Rol.ESTUDIANTE) {
            return null;
        }
        return estudianteDAO.readByRut(usuario.getRut());
    }
}
