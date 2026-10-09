package com.bibliotecaescolar.controlador;

import com.bibliotecaescolar.dao.ICategoriaDAO;
import com.bibliotecaescolar.dao.ILibroDAO;
import com.bibliotecaescolar.model.Categoria;
import com.bibliotecaescolar.model.Libro;

import java.sql.SQLException;
import java.util.List;

public class LibroControlador {
    private final ILibroDAO libroDAO;
    private final ICategoriaDAO categoriaDAO;

    public LibroControlador(ILibroDAO libroDAO, ICategoriaDAO categoriaDAO) {
        this.libroDAO = libroDAO;
        this.categoriaDAO = categoriaDAO;
    }

    public int registrarLibro(Libro libro) throws SQLException {
        return libroDAO.create(libro);
    }

    public List<Libro> listarLibros() throws SQLException {
        return libroDAO.readAll();
    }

    public List<Libro> buscarLibros(String titulo, Integer idCategoria) throws SQLException {
        return libroDAO.readByFiltro(titulo, idCategoria);
    }

    public List<Libro> listarLibrosDisponibles() throws SQLException {
        return libroDAO.readDisponibles();
    }

    public Libro buscarPorId(int id) throws SQLException {
        return libroDAO.readById(id);
    }

    public void modificarLibro(Libro libro) throws SQLException {
        libroDAO.update(libro);
    }

    public void eliminarLibro(int id) throws SQLException {
        libroDAO.delete(id);
    }

    public List<Categoria> listarCategorias() throws SQLException {
        return categoriaDAO.readAll();
    }
}
