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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para manejar la lógica del parqueadero:
 *  - Registro de entrada de vehículos.
 *  - Validación de capacidad (espacios.json).
 *  - Persistencia de registros (registros.json).
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
     * Registro automático usando datos simulados del OCR.
     * Se deja por compatibilidad, pero la lógica real está en registrarEntradaManual.
     */
    public Registro registrarEntrada() {
        String placa = OCRService.leerPlacaSimulada();
        TipoVehiculo tipoVehiculo = OCRService.detectarTipoVehiculoSimulado();
        return registrarEntradaManual(placa, tipoVehiculo);
    }

    /**
     * Registra la entrada de un vehículo usando los datos ingresados manualmente.
     *  - Valida placa y tipo.
     *  - Verifica que haya espacios disponibles.
     *  - Incrementa ocupados en espacios.json.
     *  - Crea un Registro con entrada = ahora, salida y total en null.
     *  - Guarda el registro en registros.json.
     */
    public Registro registrarEntradaManual(String placa, TipoVehiculo tipoVehiculo) {
        // 1. Validar datos recibidos
        if (placa == null || placa.trim().isEmpty()) {
            throw new IllegalStateException("La placa no puede estar vacía.");
        }
        if (tipoVehiculo == null) {
            throw new IllegalStateException("Debes seleccionar el tipo de vehículo.");
        }

        // 2. Validar capacidad del parqueadero
        EspaciosData espaciosData = leerEspacios();
        int disponibles = espaciosData.totalEspacios - espaciosData.ocupados;
        if (disponibles <= 0) {
            throw new IllegalStateException("No hay espacios disponibles en el parqueadero.");
        }

        // 3. Actualizar espacios ocupados
        espaciosData.ocupados = espaciosData.ocupados + 1;
        guardarEspacios(espaciosData);

        // 4. Crear vehículo y registro
        Vehiculo vehiculo = new Vehiculo(placa.trim().toUpperCase(), tipoVehiculo);
        LocalDateTime ahora = LocalDateTime.now();

        Registro registro = new Registro(
                vehiculo.getPlaca(),
                vehiculo.getTipoVehiculo(),
                ahora
        );

        // 5. Persistir en registros.json
        List<Registro> registros = leerRegistros();
        registros.add(registro);
        guardarRegistros(registros);

        return registro;
    }

    // ---------- Manejo de espacios.json ----------

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

    private EspaciosData leerEspacios() {
        if (!Files.exists(ESPACIOS_PATH)) {
            // Si el archivo no existe, se crea uno por defecto
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

    // ---------- Manejo de registros.json ----------

    private List<Registro> leerRegistros() {
        if (!Files.exists(REGISTROS_PATH)) {
            return new ArrayList<>();
        }

        try (Reader reader = Files.newBufferedReader(REGISTROS_PATH)) {
            java.lang.reflect.Type listType = new TypeToken<List<Registro>>() {}.getType();
            List<Registro> registros = gson.fromJson(reader, listType);
            if (registros == null) {
                registros = new ArrayList<>();
            }
            return registros;
        } catch (IOException e) {
            throw new RuntimeException("Error al leer registros.json", e);
        }
    }

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
}

/**
 * Representa la estructura del archivo espacios.json:
 * {
 *   "totalEspacios": 40,
 *   "ocupados": 0
 * }
 */
class EspaciosData {
    int totalEspacios;
    int ocupados;
}

/**
 * Adaptador para poder guardar LocalDateTime en JSON como String ISO
 * y volverlo a leer correctamente.
 */
class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

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
