package com.example.parkingaxm.services;

import com.example.parkingaxm.enums.TipoVehiculo;
import com.example.parkingaxm.models.Registro;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

public class EstadisticasService {

    private static final String REGISTROS_PATH =
            "src/main/resources/com/example/parkingaxm/data/registros.json";

    private final Gson gson;

    public EstadisticasService() {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
    }

    // ----------- Cargar registros ------------

    public List<Registro> cargarRegistros() {
        try {
            if (!Files.exists(Paths.get(REGISTROS_PATH))) return List.of();

            Reader reader = Files.newBufferedReader(Paths.get(REGISTROS_PATH));
            java.lang.reflect.Type listType = new TypeToken<List<Registro>>() {}.getType();
            List<Registro> registros = gson.fromJson(reader, listType);
            return registros == null ? List.of() : registros;

        } catch (Exception e) {
            throw new RuntimeException("Error al cargar registros para estadísticas", e);
        }
    }

    // ----------- Obtener estadísticas filtradas ------------

    public EstadisticasDTO obtenerEstadisticas(LocalDate desde, LocalDate hasta, TipoVehiculo filtroTipo) {
        List<Registro> registros = cargarRegistros();

        // Filtro de fechas
        if (desde != null) {
            registros = registros.stream()
                    .filter(r -> !r.getEntrada().toLocalDate().isBefore(desde))
                    .collect(Collectors.toList());
        }
        if (hasta != null) {
            registros = registros.stream()
                    .filter(r -> !r.getEntrada().toLocalDate().isAfter(hasta))
                    .collect(Collectors.toList());
        }

        // Filtro de tipo
        if (filtroTipo != null) {
            TipoVehiculo finalFiltro = filtroTipo;
            registros = registros.stream()
                    .filter(r -> r.getTipoVehiculo() == finalFiltro)
                    .collect(Collectors.toList());
        }

        return new EstadisticasDTO(registros);
    }

    // ----------- Adapter LocalDateTime ------------

    private static class LocalDateTimeAdapter
            implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

        @Override
        public JsonElement serialize(LocalDateTime src, java.lang.reflect.Type typeOfSrc,
                                     JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, java.lang.reflect.Type typeOfT,
                                         JsonDeserializationContext context)
                throws JsonParseException {
            return LocalDateTime.parse(json.getAsString());
        }
    }

    // ----------- DTO AVANZADO ------------

    public static class EstadisticasDTO {

        // Totales globales del rango
        public int totalVehiculos;
        public int totalCarros;
        public int totalMotos;

        // Por día, semana, mes (respecto a HOY, no al filtro)
        public long carrosHoy;
        public long motosHoy;
        public long carrosSemana;
        public long motosSemana;
        public long carrosMes;
        public long motosMes;

        // Dinero
        public double dineroHoy;
        public double dineroSemana;
        public double dineroMes;
        public double dineroTotal;

        // Tiempo de permanencia (en minutos)
        public double permanenciaPromedioMin;
        public long permanenciaMaxMin;

        // Para gráficas
        public Map<String, Long> vehiculosPorDia = new LinkedHashMap<>();
        public Map<String, Double> dineroPorDia = new LinkedHashMap<>();

        // Heatmap: díaSemana -> hora -> cantidad
        public Map<DayOfWeek, Map<Integer, Long>> heatmap = new EnumMap<>(DayOfWeek.class);

        public EstadisticasDTO(List<Registro> registros) {

            totalVehiculos = registros.size();
            totalCarros = (int) registros.stream().filter(r -> r.getTipoVehiculo() == TipoVehiculo.CARRO).count();
            totalMotos = totalVehiculos - totalCarros;

            LocalDate hoy = LocalDate.now();
            LocalDate inicioSemana = hoy.with(DayOfWeek.MONDAY);
            LocalDate inicioMes = hoy.withDayOfMonth(1);

            long sumaMin = 0;
            long countDuraciones = 0;
            long maxMin = 0;

            for (Registro r : registros) {

                LocalDateTime entrada = r.getEntrada();
                LocalDateTime salida = r.getSalida();
                LocalDate fecha = entrada.toLocalDate();
                boolean esCarro = r.getTipoVehiculo() == TipoVehiculo.CARRO;

                // ------- Vehículos y dinero por día ----------
                String keyDia = fecha.toString();
                vehiculosPorDia.merge(keyDia, 1L, Long::sum);
                dineroPorDia.merge(keyDia, r.getTotal(), Double::sum);

                // ------- Estadísticas por periodo respecto a HOY ----------
                if (fecha.equals(hoy)) {
                    if (esCarro) carrosHoy++; else motosHoy++;
                    dineroHoy += r.getTotal();
                }

                if (!fecha.isBefore(inicioSemana)) {
                    if (esCarro) carrosSemana++; else motosSemana++;
                    dineroSemana += r.getTotal();
                }

                if (!fecha.isBefore(inicioMes)) {
                    if (esCarro) carrosMes++; else motosMes++;
                    dineroMes += r.getTotal();
                }

                dineroTotal += r.getTotal();

                // ------- Permanencia ----------
                if (salida != null) {
                    long minutos = Duration.between(entrada, salida).toMinutes();
                    if (minutos > 0) {
                        sumaMin += minutos;
                        countDuraciones++;
                        if (minutos > maxMin) maxMin = minutos;
                    }
                }

                // ------- Heatmap día/hora ----------
                DayOfWeek day = fecha.getDayOfWeek();
                int hour = entrada.getHour();

                heatmap.putIfAbsent(day, new HashMap<>());
                Map<Integer, Long> horas = heatmap.get(day);
                horas.merge(hour, 1L, Long::sum);
            }

            if (countDuraciones > 0) {
                permanenciaPromedioMin = (double) sumaMin / countDuraciones;
                permanenciaMaxMin = maxMin;
            } else {
                permanenciaPromedioMin = 0;
                permanenciaMaxMin = 0;
            }
        }
    }
}
