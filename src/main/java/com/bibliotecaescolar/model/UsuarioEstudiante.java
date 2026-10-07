package com.bibliotecaescolar.model;

public class UsuarioEstudiante extends Usuario {

    public UsuarioEstudiante(Integer id, String nombre, String rut, String correo, String contrasena) {
        super(id, nombre, rut, correo, contrasena, Rol.ESTUDIANTE);
    }

    @Override
    public boolean tieneAcceso(Modulo modulo) {
        return modulo == Modulo.PRESTAMOS || modulo == Modulo.CONSULTA;
    }

    @Override
    public String getDescripcion() {
        return "Estudiante";
    }
}
