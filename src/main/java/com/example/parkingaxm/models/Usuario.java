package com.example.parkingaxm.models;

import com.example.parkingaxm.enums.Rol;

public class Usuario {
    private String username;
    private String password;
    private Rol rol;

    public Usuario(String username, String password, Rol rol) {
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Rol getRol() { return rol; }
}
