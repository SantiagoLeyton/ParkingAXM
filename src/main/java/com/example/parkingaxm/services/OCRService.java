package com.example.parkingaxm.services;

import com.example.parkingaxm.enums.TipoVehiculo;

import java.util.Random;

/**
 * Servicio de OCR SIMULADO.
 * No realiza reconocimiento real, solo devuelve datos ficticios
 * para probar el flujo del sistema.
 */
public class OCRService {

    private static final String[] PLACAS_SIMULADAS = {
            "ABC123", "XYZ987", "JKL456", "MNO321", "PQR654"
    };

    private static final Random RANDOM = new Random();

    /**
     * Simula la lectura de una placa desde una imagen.
     */
    public static String leerPlacaSimulada() {
        return PLACAS_SIMULADAS[RANDOM.nextInt(PLACAS_SIMULADAS.length)];
    }

    /**
     * Simula la detección del tipo de vehículo.
     * Alterna entre CARRO y MOTO sin lógica real.
     */
    public static TipoVehiculo detectarTipoVehiculoSimulado() {
        return RANDOM.nextBoolean() ? TipoVehiculo.CARRO : TipoVehiculo.MOTO;
    }
}
