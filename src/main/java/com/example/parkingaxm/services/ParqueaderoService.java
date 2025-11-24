package com.example.parkingaxm.services;

import com.example.parkingaxm.enums.TipoVehiculo;
import com.example.parkingaxm.models.Registro;
import com.example.parkingaxm.models.Vehiculo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio principal del módulo de registro.
 * Se encarga de:
 *  - Simular la lectura de una placa y tipo de vehículo (usando OCRService).
 *  - Validar la capacidad del parqueadero (espacios.json).
 *  - Descontar un espacio disponible al registrar la entrada.
 *  - Persistir el registro en registros.json.
 */
public class ParqueaderoService {

    private static final Path ESPACIOS_PATH =
            Paths.get("src/main/resources/com/example/parkingaxm/data/espacios.json");
    private static final Path REGISTROS_PATH =
            Paths.get("src/main/resources/com/example/parkingaxm/data/registros.json");

    private final Gson gson;

    public ParqueaderoService() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
    }

    /**
     * Registra la entrada de un vehículo al parqueadero.
     *
     * @return Registro creado y persistido.
     */
    public Registro registrarEntrada() {
        // 1. Simulación OCR (placa y tipo de vehículo)
        String placa = OCRService.leerPlacaSimulada();
        TipoVehiculo tipoVehiculo = OCRService.detectarTipoVehiculoSimulado();

        if (placa == null || placa.isBlank()) {
            throw new IllegalStateException("No se pudo leer la placa del vehículo.");
        }
        if (tipoVehiculo == null) {
            throw new IllegalStateException("No se pudo determinar el tipo de vehículo.");
        }

        // 2. Validar capacidad (espacios.json)
        EspaciosData espaciosData = leerEspacios();
        int disponibles = espaciosData.totalEspacios - espaciosData.ocupados;
        if (disponibles <= 0) {
            throw new IllegalStateException("No hay espacios disponibles en el parqueadero.");
        }

        // 3. Descontar un espacio (incrementar ocupados) y guardar cambios
        espaciosData.ocupados = espaciosData.ocupados + 1;
        guardarEspacios(espaciosData);

        // 4. Crear el vehículo y el registro
        Vehiculo vehiculo = new Vehiculo(placa.trim().toUpperCase(), tipoVehiculo);
        LocalDateTime ahora = LocalDateTime.now();
        Registro registro = new Registro(vehiculo.getPlaca(), vehiculo.getTipoVehiculo(), ahora);

        // 5. Persistir el registro en registros.json
        List<Registro> registros = leerRegistros();
        registros.add(registro);
        guardarRegistros(registros);

        return registro;
    }

    /**
     * Lee los datos de espacios disponibles desde espacios.json.
     */
    private EspaciosData leerEspacios() {
        if (!Files.exists(ESPACIOS_PATH)) {
            // Archivo inexistente: se crea con valores por defecto
            EspaciosData data = new EspaciosData();
            data.totalEspacios = 40;
            data.ocupados = 0;
            guardarEspacios(data);
            return data;
        }

        try (Reader reader = Files.newBufferedReader(ESPACIOS_PATH)) {
            EspaciosData data = gson.fromJson(reader, EspaciosData.class);
            if (data == null) {
                data = new EspaciosData();
                data.totalEspacios = 40;
                data.ocupados = 0;
            }
            return data;
        } catch (IOException e) {
            throw new RuntimeException("Error al leer espacios.json", e);
        }
    }

    /**
     * Guarda el estado de los espacios de parqueo en espacios.json.
     */
    private void guardarEspacios(EspaciosData data) {
        try {
            if (ESPACIOS_PATH.getParent() != null && !Files.exists(ESPACIOS_PATH.getParent())) {
                Files.createDirectories(ESPACIOS_PATH.getParent());
            }
            try (Writer writer = Files.newBufferedWriter(ESPACIOS_PATH)) {
                gson.toJson(data, writer);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar espacios.json", e);
        }
    }

    /**
     * Lee la lista de registros desde registros.json.
     */
    private List<Registro> leerRegistros() {
        if (!Files.exists(REGISTROS_PATH)) {
            return new ArrayList<>();
        }

        try (Reader reader = Files.newBufferedReader(REGISTROS_PATH)) {
            Type listType = new TypeToken<List<Registro>>() {}.getType();
            List<Registro> registros = gson.fromJson(reader, listType);
            return (registros != null) ? registros : new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException("Error al leer registros.json", e);
        }
    }

    /**
     * Guarda la lista completa de registros en registros.json.
     */
    private void guardarRegistros(List<Registro> registros) {
        try {
            if (REGISTROS_PATH.getParent() != null && !Files.exists(REGISTROS_PATH.getParent())) {
                Files.createDirectories(REGISTROS_PATH.getParent());
            }
            try (Writer writer = Files.newBufferedWriter(REGISTROS_PATH)) {
                gson.toJson(registros, writer);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar registros.json", e);
        }
    }

    /**
     * Clase interna para mapear el JSON de espacios.json.
     * {
     *   "totalEspacios": 40,
     *   "ocupados": 0
     * }
     */
    private static class EspaciosData {
        int totalEspacios;
        int ocupados;
    }

    /**
     * Adaptador para serializar/deserializar LocalDateTime como String ISO-8601.
     */
    private static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

        @Override
        public JsonElement serialize(LocalDateTime src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            return src == null ? null : context.serialize(src.toString());
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            if (json == null || json.isJsonNull()) {
                return null;
            }
            String value = json.getAsString();
            return LocalDateTime.parse(value);
        }
    }
}
