package com.example.parkingaxm.models;

import com.example.parkingaxm.enums.Rol;

public class Usuario {

    private String username;
    private String password;
    private Rol rol;

    public Usuario() {
    }

    public Usuario(String username, String password, Rol rol) {
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    // OJO: en un proyecto real deberías guardar hashes, pero aquí es plano.
    public void setPassword(String password) {
        this.password = password;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public boolean esAdmin() {
        return Rol.ADMIN.equals(this.rol);
    }

    public boolean esOperario() {
        return Rol.OPERARIO.equals(this.rol);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "username='" + username + '\'' +
                ", rol=" + rol +
                '}';
    }
}
