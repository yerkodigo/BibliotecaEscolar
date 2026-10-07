package com.bibliotecaescolar.model;

public enum Rol {
    BIBLIOTECARIO,
    ESTUDIANTE;

    public String getValorBD() {
        return name().toLowerCase();
    }

    public static Rol desdeValorBD(String valor) {
        return Rol.valueOf(valor.toUpperCase());
    }
}
