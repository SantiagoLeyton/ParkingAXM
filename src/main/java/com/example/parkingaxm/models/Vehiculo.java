package com.example.parkingaxm.models;

import com.example.parkingaxm.enums.TipoVehiculo;

/**
 * Representa un vehículo que ingresa al parqueadero.
 * Para el módulo de registro solo necesitamos:
 *  - Placa
 *  - Tipo de vehículo (CARRO / MOTO)
 */
public class Vehiculo {

    private String placa;
    private TipoVehiculo tipoVehiculo;

    // Constructor vacío necesario para Gson
    public Vehiculo() {
    }

    public Vehiculo(String placa) {
        this(placa, null);
    }

    public Vehiculo(String placa, TipoVehiculo tipoVehiculo) {
        this.placa = placa;
        this.tipoVehiculo = tipoVehiculo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public TipoVehiculo getTipoVehiculo() {
        return tipoVehiculo;
    }

    public void setTipoVehiculo(TipoVehiculo tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo;
    }

    @Override
    public String toString() {
        return "Vehiculo{" +
                "placa='" + placa + '\'' +
                ", tipoVehiculo=" + tipoVehiculo +
                '}';
    }
}
