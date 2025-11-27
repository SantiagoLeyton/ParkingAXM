package com.example.parkingaxm.models;

import com.example.parkingaxm.enums.TipoVehiculo;
import java.time.LocalDateTime;

public class Registro {

    private String placa;
    private TipoVehiculo tipoVehiculo;
    private LocalDateTime entrada;
    private LocalDateTime salida;
    private double total;

    public Registro(String placa, TipoVehiculo tipoVehiculo, LocalDateTime entrada) {
        this.placa = placa;
        this.tipoVehiculo = tipoVehiculo;
        this.entrada = entrada;
    }

    public String getPlaca() {
        return placa;
    }

    public TipoVehiculo getTipoVehiculo() {
        return tipoVehiculo;
    }

    public LocalDateTime getEntrada() {
        return entrada;
    }

    public LocalDateTime getSalida() {
        return salida;
    }

    public double getTotal() {
        return total;
    }

    public void setSalida(LocalDateTime salida) {
        this.salida = salida;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
