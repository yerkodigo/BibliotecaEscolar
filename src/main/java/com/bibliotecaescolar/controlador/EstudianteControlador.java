package com.bibliotecaescolar.controlador;

import com.bibliotecaescolar.dao.IEstudianteDAO;
import com.bibliotecaescolar.dao.IUsuarioDAO;
import com.bibliotecaescolar.model.Estudiante;
import com.bibliotecaescolar.model.UsuarioEstudiante;

import java.sql.SQLException;
import java.util.List;

public class EstudianteControlador {
    private final IEstudianteDAO estudianteDAO;
    private final IUsuarioDAO usuarioDAO;

    public EstudianteControlador(IEstudianteDAO estudianteDAO, IUsuarioDAO usuarioDAO) {
        this.estudianteDAO = estudianteDAO;
        this.usuarioDAO = usuarioDAO;
    }

    public int registrarEstudiante(Estudiante estudiante) throws SQLException {
        return estudianteDAO.create(estudiante);
    }

    public int registrarEstudianteConAcceso(Estudiante estudiante, String contrasena) throws SQLException {
        if (usuarioDAO.readByRut(estudiante.getRut()) != null) {
            throw new SQLException("Ya existe un usuario del sistema con el RUT " + estudiante.getRut());
        }

        int idEstudiante = estudianteDAO.create(estudiante);
        UsuarioEstudiante usuario = new UsuarioEstudiante(null, estudiante.getNombre(), estudiante.getRut(),
                estudiante.getCorreo(), contrasena);
        usuarioDAO.create(usuario);
        return idEstudiante;
    }

    public List<Estudiante> listarEstudiantes() throws SQLException {
        return estudianteDAO.readAll();
    }

    public Estudiante buscarPorId(int id) throws SQLException {
        return estudianteDAO.readById(id);
    }

    public void modificarEstudiante(Estudiante estudiante) throws SQLException {
        estudianteDAO.update(estudiante);
    }

    public void eliminarEstudiante(int id) throws SQLException {
        estudianteDAO.delete(id);
    }
}
