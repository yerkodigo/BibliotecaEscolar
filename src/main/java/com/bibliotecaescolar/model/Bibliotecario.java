package com.bibliotecaescolar.model;

public class Bibliotecario extends Usuario {

    public Bibliotecario(Integer id, String nombre, String rut, String correo, String contrasena) {
        super(id, nombre, rut, correo, contrasena, Rol.BIBLIOTECARIO);
    }

    @Override
    public boolean tieneAcceso(Modulo modulo) {
        return true;
    }

    @Override
    public String getDescripcion() {
        return "Bibliotecario";
    }
}
