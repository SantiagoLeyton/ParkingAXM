package com.example.parkingaxm.models;

import com.example.parkingaxm.enums.TipoVehiculo;

import java.time.LocalDateTime;

public class Registro {

    private String placa;
    private String tipoVehiculo;
    private LocalDateTime entrada;
    private LocalDateTime salida;
    private double total;

<<<<<<< HEAD
    public Registro(String placa, String tipoVehiculo, LocalDateTime entrada) {
=======
    public Registro(String placa, TipoVehiculo tipoVehiculo, LocalDateTime entrada) {
>>>>>>> 5d2b053af1da132cc257008c2efdc3fd03402308
        this.placa = placa;
        this.tipoVehiculo = tipoVehiculo;
        this.entrada = entrada;
    }

    public String getPlaca() { return placa; }
    public String getTipoVehiculo() { return tipoVehiculo; }
    public LocalDateTime getEntrada() { return entrada; }
    public LocalDateTime getSalida() { return salida; }
    public double getTotal() { return total; }

    public void setSalida(LocalDateTime salida) { this.salida = salida; }
    public void setTotal(double total) { this.total = total; }

    public String getTipoVehiculo() {
        final String o = null;
        return o;
    }
}
