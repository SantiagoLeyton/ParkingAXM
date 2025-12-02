package com.example.parkingaxm.utils;

import com.example.parkingaxm.models.Registro;
import com.example.parkingaxm.utils.TimeUtils.LocalDateTimeAdapter;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    private static final Path REGISTROS_PATH =
            Paths.get("src/main/resources/com/example/parkingaxm/data/registros.json");

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .setPrettyPrinting()
            .create();

    public static List<Registro> cargarRegistros() {
        try {
            if (!Files.exists(REGISTROS_PATH)) {
                return new ArrayList<>();
            }

            Reader reader = Files.newBufferedReader(REGISTROS_PATH);
            return gson.fromJson(reader, new TypeToken<List<Registro>>() {}.getType());

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public static void guardarRegistros(List<Registro> registros) {
        try {
            Writer writer = Files.newBufferedWriter(REGISTROS_PATH);
            gson.toJson(registros, writer);
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException("Error guardando registros.json", e);
        }
    }
}
