package com.tortilleria.app_tortilleria.dto;

import com.tortilleria.app_tortilleria.model.Usuario;

public class UsuarioDTO {
    private String nombre;
    private String email;
    private String direccion;
    private String telefono;
    private String token;

    public UsuarioDTO(Usuario usuario) {
        this.nombre = usuario.getNombre();
        this.email = usuario.getEmail();
        this.direccion = usuario.getDireccion();
        this.telefono = usuario.getTelefono();
    }

    public UsuarioDTO(Usuario usuario, String token) {
        this.nombre = usuario.getNombre();
        this.email = usuario.getEmail();
        this.direccion = usuario.getDireccion();
        this.telefono = usuario.getTelefono();
        this.token = token;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getTelefono() {
        return telefono;
    }
}
