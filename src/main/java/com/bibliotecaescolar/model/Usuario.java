package com.bibliotecaescolar.model;

public abstract class Usuario extends Persona {
    private String contrasena;
    private Rol rol;

    public Usuario(Integer id, String nombre, String rut, String correo, String contrasena, Rol rol) {
        super(id, nombre, rut, correo);
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public abstract boolean tieneAcceso(Modulo modulo);

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}
