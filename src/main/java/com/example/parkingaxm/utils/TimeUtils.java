package com.example.parkingaxm.utils;

import com.example.parkingaxm.enums.TipoVehiculo;
import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;

public class TimeUtils {

    public static long calcularMinutos(LocalDateTime entrada, LocalDateTime salida) {
        return Duration.between(entrada, salida).toMinutes();
    }

    public static long calcularLapsos30(long minutos) {
        if (minutos <= 0) return 1;
        return (long) Math.ceil(minutos / 30.0);
    }

    public static double calcularTotal(TipoVehiculo tipo, long lapsos) {
        switch (tipo) {
            case CARRO: return 2500 + (lapsos * 1800);
            case MOTO:  return 1600 + (lapsos * 1200);
            default:    return 0;
        }
    }

    public static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

        @Override
        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return LocalDateTime.parse(json.getAsString());
        }
    }
}
