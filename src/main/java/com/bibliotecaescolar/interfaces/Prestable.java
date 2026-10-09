package com.bibliotecaescolar.interfaces;

public interface Prestable {
    boolean hayDisponibilidad();

    void descontarStock();

    void reponerStock();
}
