package com.example.parkingaxm.models;

public class EspacioParqueo {
    private int totalEspacios;
    private int ocupados;

    public EspacioParqueo(int totalEspacios, int ocupados) {
        this.totalEspacios = totalEspacios;
        this.ocupados = ocupados;
    }

    public int getDisponibles() { return totalEspacios - ocupados; }
}
