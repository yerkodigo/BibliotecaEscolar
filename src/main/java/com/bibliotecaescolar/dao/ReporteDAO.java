package com.bibliotecaescolar.dao;

import com.bibliotecaescolar.config.DatabaseConnection;
import com.bibliotecaescolar.model.Prestamo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReporteDAO implements IReporteDAO {
    private final IPrestamoDAO prestamoDAO;

    public ReporteDAO(IPrestamoDAO prestamoDAO) {
        this.prestamoDAO = prestamoDAO;
    }

    @Override
    public List<Object[]> librosMasPrestados() throws SQLException {
        String sql = """
                SELECT l.titulo, l.autor, COUNT(p.id) AS total_prestamos
                FROM libros l
                JOIN prestamos p ON p.id_libro = l.id
                GROUP BY l.id, l.titulo, l.autor
                ORDER BY total_prestamos DESC, l.titulo
                """;
        List<Object[]> resultado = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                resultado.add(new Object[]{
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getInt("total_prestamos")
                });
            }
        }
        return resultado;
    }

    @Override
    public List<Prestamo> historialPorEstudiante(int idEstudiante) throws SQLException {
        return prestamoDAO.readByEstudiante(idEstudiante);
    }

    @Override
    public List<Prestamo> librosEnPrestamo() throws SQLException {
        return prestamoDAO.readActivos();
    }
}
