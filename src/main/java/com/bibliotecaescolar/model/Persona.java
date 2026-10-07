package com.bibliotecaescolar.model;

public abstract class Persona {
    private Integer id;
    private String nombre;
    private String rut;
    private String correo;

    public Persona(Integer id, String nombre, String rut, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.rut = rut;
        this.correo = correo;
    }

    public Persona() {}

    public abstract String getDescripcion();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    @Override
    public String toString() {
        return nombre + " (" + rut + ")";
    }
}
