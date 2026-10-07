package com.bibliotecaescolar.model;

import com.bibliotecaescolar.interfaces.Prestable;

public class Libro implements Prestable {
    private Integer id;
    private String titulo;
    private String autor;
    private String isbn;
    private String editorial;
    private Integer stock;
    private Integer idCategoria;

    private String nombreCategoria;

    public Libro(Integer id, String titulo, String autor, String isbn, String editorial, Integer stock, Integer idCategoria) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.editorial = editorial;
        this.stock = stock;
        this.idCategoria = idCategoria;
    }

    public Libro() {}

    @Override
    public boolean hayDisponibilidad() {
        return stock != null && stock > 0;
    }

    @Override
    public void descontarStock() {
        if (hayDisponibilidad()) {
            stock--;
        }
    }

    @Override
    public void reponerStock() {
        stock++;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    @Override
    public String toString() {
        return titulo + " (stock: " + stock + ")";
    }
}
