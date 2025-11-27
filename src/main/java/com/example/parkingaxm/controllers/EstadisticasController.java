package com.example.parkingaxm.controllers;

import com.example.parkingaxm.services.EstadisticasService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class EstadisticasController {

    @FXML private Label lblDia;
    @FXML private Label lblSemana;
    @FXML private Label lblMes;
    @FXML private Label lblDinero;

    private EstadisticasService service = new EstadisticasService();

    @FXML
    public void initialize() {
        cargarDatos();
    }

    private void cargarDatos() {
        var stats = service.obtenerEstadisticas();

        lblDia.setText(
                "Hoy: " + stats.dia +
                        " vehículos (" + stats.carrosDia + " carros, " + stats.motosDia + " motos)"
        );

        lblSemana.setText(
                "Semana: " + stats.semana +
                        " vehículos (" + stats.carrosSemana + " carros, " + stats.motosSemana + " motos)"
        );

        lblMes.setText(
                "Mes: " + stats.mes +
                        " vehículos (" + stats.carrosMes + " carros, " + stats.motosMes + " motos)"
        );

        lblDinero.setText("Ingresos mes: $" + (long) stats.dineroMes);
    }
}
