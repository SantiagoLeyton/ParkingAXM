package com.example.parkingaxm.utils;

import com.example.parkingaxm.enums.Rol;
import com.example.parkingaxm.models.Usuario;

public class SessionManager {

    private static Usuario usuarioActual;

    public static void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static Rol getRolActual() {
        return (usuarioActual != null) ? usuarioActual.getRol() : null;
    }

    public static void limpiar() {
        usuarioActual = null;
    }
}
