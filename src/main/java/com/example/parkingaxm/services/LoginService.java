package com.example.parkingaxm.services;

import com.example.parkingaxm.models.Usuario;
import com.example.parkingaxm.enums.Rol;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.util.List;

public class LoginService {

    public Usuario login(String user, String pass) {
        List<Usuario> usuarios = cargarUsuarios();
        if (usuarios == null) return null;

        return usuarios.stream()
                .filter(u -> u.getUsername().equals(user) && u.getPassword().equals(pass))
                .findFirst()
                .orElse(null);
    }

    private List<Usuario> cargarUsuarios() {
        try {
            Gson gson = new Gson();
            InputStreamReader reader = new InputStreamReader(
                    getClass().getResourceAsStream("/com/example/parkingaxm/data/usuarios.json")
            );

            return gson.fromJson(reader, new TypeToken<List<Usuario>>(){}.getType());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
