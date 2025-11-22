package com.example.parkingaxm.controllers;

// Ajusta el package según tu proyecto, por ejemplo:
// package controllers;

import com.example.parkingaxm.enums.Rol;
import com.example.parkingaxm.models.Usuario;
import com.example.parkingaxm.services.UsuarioService;

public class LoginController {

    private final UsuarioService usuarioService = new UsuarioService();

    /**
     * Lógica de login a nivel de controlador (sin UI todavía).
     * Devuelve el Usuario autenticado o lanza IllegalArgumentException con el mensaje de error.
     */
    public Usuario iniciarSesion(String username, String password) {
        return usuarioService.iniciarSesion(username, password);
    }

    /**
     * Devuelve el rol del usuario autenticado.
     */
    public Rol obtenerRol(Usuario usuario) {
        if (usuario == null) return null;
        return usuario.getRol();
    }

    /**
     * Atajo: indica si las credenciales corresponden al ADMIN.
     */
    public boolean loginComoAdmin(String username, String password) {
        Usuario usuario = usuarioService.iniciarSesion(username, password);
        return usuario != null && usuario.esAdmin();
    }

    /**
     * Atajo: indica si las credenciales corresponden a un OPERARIO.
     */
    public boolean loginComoOperario(String username, String password) {
        Usuario usuario = usuarioService.iniciarSesion(username, password);
        return usuario != null && usuario.esOperario();
    }

    // ================= FUTURAS ACCIONES DE ADMIN (NO UI) =================

    /**
     * Métodos que luego usarás SOLO desde el panel de administrador:
     *  - crearOperario
     *  - editarOperario
     *  - eliminarOperario
     *
     * Aquí solo exponemos la lógica ya implementada en UsuarioService.
     */

    public Usuario crearOperario(String username, String password) {
        return usuarioService.crearOperario(username, password);
    }

    public Usuario editarOperario(String usernameActual, String nuevoUsername, String nuevaPassword) {
        return usuarioService.editarOperario(usernameActual, nuevoUsername, nuevaPassword);
    }

    public void eliminarOperario(String username) {
        usuarioService.eliminarOperario(username);
    }
}

