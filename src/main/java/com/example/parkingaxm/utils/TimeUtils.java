package com.example.parkingaxm.utils;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeUtils {
    public static long horasEntre(LocalDateTime inicio, LocalDateTime fin) {
        return Duration.between(inicio, fin).toHours();
    }
}
