package com.bibliotecaescolar.dao;

import com.bibliotecaescolar.config.DatabaseConnection;
import com.bibliotecaescolar.model.Prestamo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO implements IPrestamoDAO {
    private static final String SELECT_PRESTAMOS = """
            SELECT p.id, p.id_estudiante, p.id_libro, p.fecha_prestamo, p.fecha_devolucion, p.devuelto,
                   e.nombre AS nombre_estudiante, l.titulo AS titulo_libro
            FROM prestamos p
            JOIN estudiantes e ON e.id = p.id_estudiante
            JOIN libros l ON l.id = p.id_libro
            """;

    private Prestamo crearPrestamo(ResultSet rs) throws SQLException {
        Prestamo prestamo = new Prestamo(
                rs.getInt("id"),
                rs.getInt("id_estudiante"),
                rs.getInt("id_libro"),
                rs.getDate("fecha_prestamo").toLocalDate(),
                rs.getDate("fecha_devolucion").toLocalDate(),
                rs.getBoolean("devuelto")
        );
        prestamo.setNombreEstudiante(rs.getString("nombre_estudiante"));
        prestamo.setTituloLibro(rs.getString("titulo_libro"));
        return prestamo;
    }

    private List<Prestamo> ejecutarConsulta(String sql, Integer parametro) throws SQLException {
        List<Prestamo> prestamos = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (parametro != null) {
                stmt.setInt(1, parametro);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    prestamos.add(crearPrestamo(rs));
                }
            }
        }
        return prestamos;
    }

    @Override
    public int registrarPrestamo(Prestamo prestamo) throws SQLException {
        String sqlStock = "UPDATE libros SET stock = stock - 1 WHERE id = ? AND stock > 0";
        String sqlPrestamo = "INSERT INTO prestamos (id_estudiante, id_libro, fecha_prestamo, fecha_devolucion, devuelto) VALUES (?, ?, ?, ?, FALSE)";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        conn.setAutoCommit(false);
        try (PreparedStatement stmtStock = conn.prepareStatement(sqlStock);
             PreparedStatement stmtPrestamo = conn.prepareStatement(sqlPrestamo, Statement.RETURN_GENERATED_KEYS)) {
            stmtStock.setInt(1, prestamo.getIdLibro());
            if (stmtStock.executeUpdate() != 1) {
                throw new SQLException("El libro seleccionado no tiene stock disponible.");
            }

            stmtPrestamo.setInt(1, prestamo.getIdEstudiante());
            stmtPrestamo.setInt(2, prestamo.getIdLibro());
            stmtPrestamo.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
            stmtPrestamo.setDate(4, Date.valueOf(prestamo.getFechaDevolucion()));
            stmtPrestamo.executeUpdate();

            int idGenerado;
            try (ResultSet claves = stmtPrestamo.getGeneratedKeys()) {
                if (!claves.next()) {
                    throw new SQLException("No se obtuvo el id generado para el préstamo.");
                }
                idGenerado = claves.getInt(1);
            }

            conn.commit();
            return idGenerado;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    @Override
    public List<Prestamo> readAll() throws SQLException {
        return ejecutarConsulta(SELECT_PRESTAMOS + " ORDER BY p.id", null);
    }

    @Override
    public List<Prestamo> readActivos() throws SQLException {
        return ejecutarConsulta(SELECT_PRESTAMOS + " WHERE p.devuelto = FALSE ORDER BY p.fecha_devolucion", null);
    }

    @Override
    public List<Prestamo> readByEstudiante(int idEstudiante) throws SQLException {
        return ejecutarConsulta(SELECT_PRESTAMOS + " WHERE p.id_estudiante = ? ORDER BY p.fecha_prestamo DESC", idEstudiante);
    }

    @Override
    public Prestamo readById(int id) throws SQLException {
        List<Prestamo> prestamos = ejecutarConsulta(SELECT_PRESTAMOS + " WHERE p.id = ?", id);
        if (prestamos.isEmpty()) {
            return null;
        }
        return prestamos.get(0);
    }

    @Override
    public void registrarDevolucion(Prestamo prestamo) throws SQLException {
        String sqlPrestamo = "UPDATE prestamos SET devuelto = TRUE WHERE id = ? AND devuelto = FALSE";
        String sqlStock = "UPDATE libros SET stock = stock + 1 WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        conn.setAutoCommit(false);
        try (PreparedStatement stmtPrestamo = conn.prepareStatement(sqlPrestamo);
             PreparedStatement stmtStock = conn.prepareStatement(sqlStock)) {
            stmtPrestamo.setInt(1, prestamo.getId());
            if (stmtPrestamo.executeUpdate() != 1) {
                throw new SQLException("El préstamo #" + prestamo.getId() + " ya fue devuelto.");
            }

            stmtStock.setInt(1, prestamo.getIdLibro());
            stmtStock.executeUpdate();

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
}
