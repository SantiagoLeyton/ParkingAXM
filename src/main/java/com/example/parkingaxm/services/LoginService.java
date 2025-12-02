package com.example.parkingaxm.services;

import com.example.parkingaxm.models.Usuario;

public class LoginService {

    // Usamos el mismo servicio que maneja el JSON: data/usuarios.json
    private final UsuarioService usuarioService = new UsuarioService();

    /**
     * Intenta iniciar sesión.
     * Devuelve el Usuario si las credenciales son correctas.
     * Devuelve null si son incorrectas.
     */
    public Usuario login(String user, String pass) {
        try {
            // Este método ya valida usuario/contraseña y lanza IllegalArgumentException si están mal
            return usuarioService.iniciarSesion(user, pass);
        } catch (IllegalArgumentException e) {
            // Credenciales inválidas -> devolvemos null (el controlador muestra el error)
            return null;
        }
    }
}
