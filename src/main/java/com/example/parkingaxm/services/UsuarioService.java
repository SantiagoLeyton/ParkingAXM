package com.example.parkingaxm.services;

// Ajusta el package según tu proyecto, por ejemplo:
// package services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.example.parkingaxm.enums.Rol;
import com.example.parkingaxm.models.Usuario;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class UsuarioService {

    private static final Path RUTA_USUARIOS = Paths.get("data", "usuarios.json");

    private final Gson gson;

    public UsuarioService() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        inicializarArchivoUsuarios();
    }

    // ======================== PÚBLICO (LÓGICA DE LOGIN) ========================

    /**
     * Inicia sesión y devuelve el Usuario autenticado.
     * Lanza IllegalArgumentException si credenciales no válidas.
     */
    public Usuario iniciarSesion(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Usuario y contraseña son obligatorios.");
        }

        String userTrim = username.trim();
        String passTrim = password.trim();

        if (userTrim.isEmpty() || passTrim.isEmpty()) {
            throw new IllegalArgumentException("Usuario y contraseña no pueden estar vacíos.");
        }

        List<Usuario> usuarios = cargarUsuarios();

        for (Usuario u : usuarios) {
            if (u.getUsername() != null &&
                    u.getUsername().equals(userTrim) &&
                    u.getPassword() != null &&
                    u.getPassword().equals(passTrim)) {
                return u; // Login correcto
            }
        }

        throw new IllegalArgumentException("Usuario o contraseña incorrectos.");
    }

    /**
     * Valida credenciales sin devolver el usuario (por si solo quieres true/false).
     */
    public boolean validarCredenciales(String username, String password) {
        try {
            iniciarSesion(username, password);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Retorna true si el usuario con ese username es ADMIN.
     */
    public boolean esAdmin(String username) {
        Usuario u = buscarPorUsername(username);
        return u != null && u.esAdmin();
    }

    /**
     * Retorna true si el usuario con ese username es OPERARIO.
     */
    public boolean esOperario(String username) {
        Usuario u = buscarPorUsername(username);
        return u != null && u.esOperario();
    }

    // ======================== CRUD OPERARIOS (LÓGICA SOLO) ========================

    /**
     * Crea un nuevo operario.
     * - username debe ser único
     * - se crea siempre con rol OPERARIO
     */
    public Usuario crearOperario(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username y contraseña son obligatorios.");
        }

        String userTrim = username.trim();
        String passTrim = password.trim();

        if (userTrim.isEmpty() || passTrim.isEmpty()) {
            throw new IllegalArgumentException("Username y contraseña no pueden estar vacíos.");
        }

        List<Usuario> usuarios = cargarUsuarios();

        // Validar que no exista un usuario con ese username
        for (Usuario u : usuarios) {
            if (u.getUsername().equalsIgnoreCase(userTrim)) {
                throw new IllegalArgumentException("Ya existe un usuario con ese username.");
            }
        }

        Usuario nuevo = new Usuario(userTrim, passTrim, Rol.OPERARIO);
        usuarios.add(nuevo);
        guardarUsuarios(usuarios);

        return nuevo;
    }

    /**
     * Edita un operario existente (username y/o contraseña).
     * No permite editar la cuenta ADMIN.
     */
    public Usuario editarOperario(String usernameActual, String nuevoUsername, String nuevaPassword) {
        if (usernameActual == null) {
            throw new IllegalArgumentException("Debe indicar el username actual.");
        }

        List<Usuario> usuarios = cargarUsuarios();
        Usuario objetivo = null;

        for (Usuario u : usuarios) {
            if (u.getUsername().equals(usernameActual)) {
                objetivo = u;
                break;
            }
        }

        if (objetivo == null) {
            throw new IllegalArgumentException("No existe un usuario con username: " + usernameActual);
        }

        if (objetivo.esAdmin()) {
            throw new IllegalArgumentException("No está permitido editar la cuenta ADMIN desde aquí.");
        }

        // Validar nuevo username si cambia
        if (nuevoUsername != null && !nuevoUsername.trim().isEmpty()
                && !nuevoUsername.equals(usernameActual)) {

            String userTrim = nuevoUsername.trim();

            for (Usuario u : usuarios) {
                if (!u.getUsername().equals(usernameActual) &&
                        u.getUsername().equalsIgnoreCase(userTrim)) {
                    throw new IllegalArgumentException("Ya existe otro usuario con el username: " + userTrim);
                }
            }
            objetivo.setUsername(userTrim);
        }

        if (nuevaPassword != null && !nuevaPassword.trim().isEmpty()) {
            objetivo.setPassword(nuevaPassword.trim());
        }

        guardarUsuarios(usuarios);
        return objetivo;
    }

    /**
     * Elimina un operario por username.
     * No permite eliminar la cuenta ADMIN.
     */
    public void eliminarOperario(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Debe indicar el username.");
        }

        List<Usuario> usuarios = cargarUsuarios();
        Usuario objetivo = null;

        for (Usuario u : usuarios) {
            if (u.getUsername().equals(username)) {
                objetivo = u;
                break;
            }
        }

        if (objetivo == null) {
            throw new IllegalArgumentException("No existe un usuario con username: " + username);
        }

        if (objetivo.esAdmin()) {
            throw new IllegalArgumentException("No está permitido eliminar la cuenta ADMIN.");
        }

        usuarios.remove(objetivo);
        guardarUsuarios(usuarios);
    }

    /**
     * Devuelve la lista de operarios (sin incluir ADMIN).
     */
    public List<Usuario> listarOperarios() {
        List<Usuario> usuarios = cargarUsuarios();
        List<Usuario> operarios = new ArrayList<>();

        for (Usuario u : usuarios) {
            if (u.esOperario()) {
                operarios.add(u);
            }
        }
        return operarios;
    }

    // ======================== PRIVADO (PERSISTENCIA JSON) ========================

    /**
     * Garantiza que:
     *  - exista usuarios.json
     *  - exista exactamente UNA cuenta admin:
     *      username: "admin"
     *      password: "1234"
     *      rol: ADMIN
     */
    private void inicializarArchivoUsuarios() {
        List<Usuario> usuarios = cargarUsuarios();

        // Eliminar cualquier ADMIN existente
        usuarios.removeIf(Usuario::esAdmin);

        // Agregar la única cuenta ADMIN requerida
        Usuario admin = new Usuario("admin", "1234", Rol.ADMIN);
        usuarios.add(admin);

        // Guardar
        guardarUsuarios(usuarios);
    }

    private List<Usuario> cargarUsuarios() {
        try {
            if (!Files.exists(RUTA_USUARIOS)) {
                return new ArrayList<>();
            }

            try (BufferedReader reader = Files.newBufferedReader(RUTA_USUARIOS, StandardCharsets.UTF_8)) {
                Type listType = new TypeToken<List<Usuario>>() {}.getType();
                List<Usuario> usuarios = gson.fromJson(reader, listType);
                if (usuarios == null) {
                    return new ArrayList<>();
                }
                return usuarios;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void guardarUsuarios(List<Usuario> usuarios) {
        try {
            if (!Files.exists(RUTA_USUARIOS.getParent())) {
                Files.createDirectories(RUTA_USUARIOS.getParent());
            }
            if (!Files.exists(RUTA_USUARIOS)) {
                Files.createFile(RUTA_USUARIOS);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(RUTA_USUARIOS, StandardCharsets.UTF_8)) {
                gson.toJson(usuarios, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Usuario buscarPorUsername(String username) {
        if (username == null) return null;
        String userTrim = username.trim();

        List<Usuario> usuarios = cargarUsuarios();
        for (Usuario u : usuarios) {
            if (u.getUsername() != null && u.getUsername().equals(userTrim)) {
                return u;
            }
        }
        return null;
    }
}

