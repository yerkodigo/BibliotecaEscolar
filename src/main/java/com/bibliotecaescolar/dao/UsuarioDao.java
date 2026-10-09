package com.bibliotecaescolar.dao;

import com.bibliotecaescolar.config.DatabaseConnection;
import com.bibliotecaescolar.model.Bibliotecario;
import com.bibliotecaescolar.model.Rol;
import com.bibliotecaescolar.model.Usuario;
import com.bibliotecaescolar.model.UsuarioEstudiante;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDao implements IUsuarioDAO {

    private Usuario crearUsuario(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        String nombre = rs.getString("nombre");
        String rut = rs.getString("rut");
        String correo = rs.getString("correo");
        String contrasena = rs.getString("contraseña");
        Rol rol = Rol.desdeValorBD(rs.getString("rol"));

        Usuario usuario;
        switch (rol) {
            case BIBLIOTECARIO -> usuario = new Bibliotecario(id, nombre, rut, correo, contrasena);
            default -> usuario = new UsuarioEstudiante(id, nombre, rut, correo, contrasena);
        }
        return usuario;
    }

    @Override
    public Usuario autenticar(String rutOCorreo, String contrasena) throws SQLException {
        String sql = "SELECT id, nombre, rut, correo, `contraseña`, rol FROM usuarios WHERE (rut = ? OR correo = ?) AND `contraseña` = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, rutOCorreo);
            stmt.setString(2, rutOCorreo);
            stmt.setString(3, contrasena);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return crearUsuario(rs);
                }
            }
        }
        return null;
    }

    @Override
    public Usuario readByRut(String rut) throws SQLException {
        String sql = "SELECT id, nombre, rut, correo, `contraseña`, rol FROM usuarios WHERE rut = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, rut);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return crearUsuario(rs);
                }
            }
        }
        return null;
    }

    @Override
    public int create(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre, rut, correo, `contraseña`, rol) VALUES (?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getRut());
            stmt.setString(3, usuario.getCorreo());
            stmt.setString(4, usuario.getContrasena());
            stmt.setString(5, usuario.getRol().getValorBD());
            stmt.executeUpdate();

            try (ResultSet claves = stmt.getGeneratedKeys()) {
                if (claves.next()) {
                    return claves.getInt(1);
                }
            }
        }
        throw new SQLException("No se obtuvo el id generado para el usuario.");
    }

    @Override
    public List<Usuario> readAll() throws SQLException {
        String sql = "SELECT id, nombre, rut, correo, `contraseña`, rol FROM usuarios ORDER BY id";
        List<Usuario> usuarios = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                usuarios.add(crearUsuario(rs));
            }
        }
        return usuarios;
    }

    @Override
    public Usuario readById(int id) throws SQLException {
        String sql = "SELECT id, nombre, rut, correo, `contraseña`, rol FROM usuarios WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return crearUsuario(rs);
                }
            }
        }
        return null;
    }

    @Override
    public void update(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET nombre = ?, rut = ?, correo = ?, `contraseña` = ?, rol = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getRut());
            stmt.setString(3, usuario.getCorreo());
            stmt.setString(4, usuario.getContrasena());
            stmt.setString(5, usuario.getRol().getValorBD());
            stmt.setInt(6, usuario.getId());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("No existe un usuario con id " + usuario.getId());
            }
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("No existe un usuario con id " + id);
            }
        }
    }
}
