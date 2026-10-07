package com.bibliotecaescolar.model;

public class Estudiante extends Persona {
    private String curso;

    public Estudiante(Integer id, String nombre, String rut, String curso, String correo) {
        super(id, nombre, rut, correo);
        this.curso = curso;
    }

    public Estudiante() {}

    @Override
    public String getDescripcion() {
        return "Estudiante de " + curso;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }
}
