package com.bibliotecaescolar.dao;

import com.bibliotecaescolar.config.DatabaseConnection;
import com.bibliotecaescolar.model.Libro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO implements ILibroDAO {
//    para no tener que repetir codigo
    private static final String SELECT_LIBROS = """
            SELECT l.id, l.titulo, l.autor, l.isbn, l.editorial, l.stock, l.id_categoria, c.nombre AS nombre_categoria
            FROM libros l
            LEFT JOIN categorias c ON c.id = l.id_categoria
            """;

    private Libro crearLibro(ResultSet rs) throws SQLException {
        Libro libro = new Libro(
                rs.getInt("id"),
                rs.getString("titulo"),
                rs.getString("autor"),
                rs.getString("isbn"),
                rs.getString("editorial"),
                rs.getInt("stock"),
                rs.getInt("id_categoria")
        );
        libro.setNombreCategoria(rs.getString("nombre_categoria"));
        return libro;
    }

    @Override
    public int create(Libro libro) throws SQLException {
        String sql = "INSERT INTO libros (titulo, autor, isbn, editorial, stock, id_categoria) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, libro.getTitulo());
            stmt.setString(2, libro.getAutor());
            stmt.setString(3, libro.getIsbn());
            stmt.setString(4, libro.getEditorial());
            stmt.setInt(5, libro.getStock());
            stmt.setInt(6, libro.getIdCategoria());
            stmt.executeUpdate();

            try (ResultSet claves = stmt.getGeneratedKeys()) {
                if (claves.next()) {
                    return claves.getInt(1);
                }
            }
        }
        throw new SQLException("No se obtuvo el id generado para el libro.");
    }

    @Override
    public List<Libro> readAll() throws SQLException {
        String sql = SELECT_LIBROS + " ORDER BY l.id";
        List<Libro> libros = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                libros.add(crearLibro(rs));
            }
        }
        return libros;
    }

    @Override
    public Libro readById(int id) throws SQLException {
        String sql = SELECT_LIBROS + " WHERE l.id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return crearLibro(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Libro> readByFiltro(String titulo, Integer idCategoria) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT_LIBROS + " WHERE 1=1");

        if (titulo != null) {
            sql.append(" AND l.titulo LIKE ?");
        }
        if (idCategoria != null) {
            sql.append(" AND l.id_categoria = ?");
        }
        sql.append(" ORDER BY l.id");

        List<Libro> libros = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            if (titulo != null) {
                stmt.setString(1, "%" + titulo + "%");
            }
            if (idCategoria != null) {
                stmt.setInt(2, idCategoria);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    libros.add(crearLibro(rs));
                }
            }
        }
        return libros;
    }

    @Override
    public List<Libro> readDisponibles() throws SQLException {
        String sql = SELECT_LIBROS + " WHERE l.stock > 0 ORDER BY l.titulo";
        List<Libro> libros = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                libros.add(crearLibro(rs));
            }
        }
        return libros;
    }

    @Override
    public void update(Libro libro) throws SQLException {
        String sql = "UPDATE libros SET titulo = ?, autor = ?, isbn = ?, editorial = ?, stock = ?, id_categoria = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, libro.getTitulo());
            stmt.setString(2, libro.getAutor());
            stmt.setString(3, libro.getIsbn());
            stmt.setString(4, libro.getEditorial());
            stmt.setInt(5, libro.getStock());
            stmt.setInt(6, libro.getIdCategoria());
            stmt.setInt(7, libro.getId());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("No existe un libro con id " + libro.getId());
            }
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM libros WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("No existe un libro con id " + id);
            }
        }
    }
}
