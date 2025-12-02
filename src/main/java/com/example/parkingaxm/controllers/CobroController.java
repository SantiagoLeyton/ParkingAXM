package com.example.parkingaxm.controllers;

import com.example.parkingaxm.enums.TipoVehiculo;
import com.example.parkingaxm.models.Registro;
import com.example.parkingaxm.utils.FileManager;
import com.example.parkingaxm.utils.TimeUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.time.LocalDateTime;
import java.util.List;


public class CobroController {

    @FXML
    private TextField txtPlaca;

    @FXML
    private void onCalcularCobro() {

        String placa = txtPlaca.getText().trim().toUpperCase();

        if (placa.isEmpty()) {
            mostrarError("Debe ingresar una placa.");
            return;
        }

        // Cargar registros
        List<Registro> registros = FileManager.cargarRegistros();

        // Buscar registro activo
        Registro reg = registros.stream()
                .filter(r -> r.getPlaca().equalsIgnoreCase(placa))
                .findFirst()
                .orElse(null);

        if (reg == null) {
            mostrarError("No existe un registro activo para esa placa.");
            return;
        }

        if (reg.getSalida() != null) {
            mostrarError("Este vehículo ya fue cobrado.");
            return;
        }

        // Hora de salida
        LocalDateTime salida = LocalDateTime.now();

        // Tiempo transcurrido
        long minutos = TimeUtils.calcularMinutos(reg.getEntrada(), salida);
        long lapsos = TimeUtils.calcularLapsos30(minutos);

        // Tipo del vehículo
        TipoVehiculo tipo = reg.getTipoVehiculo();

        if (tipo == null) {
            mostrarError("El registro no tiene un tipo de vehículo asignado.");
            return;
        }

        // Total según tipo
        double total = TimeUtils.calcularTotal(tipo, lapsos);

        // Actualizar registro
        reg.setSalida(salida);
        reg.setTotal(total);

        // Guardar cambios
        FileManager.guardarRegistros(registros);

        mostrarInfo(
                "COBRO REALIZADO\n\n" +
                        "Placa: " + reg.getPlaca() + "\n" +
                        "Tipo: " + tipo + "\n" +
                        "Minutos: " + minutos + "\n" +
                        "Lapsos de 30 min: " + lapsos + "\n" +
                        "TOTAL A PAGAR: $" + total
        );
    }

    // =======================================================
    // Métodos auxiliares
    // =======================================================

    private void mostrarError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg);
        a.setHeaderText(null);
        a.show();
    }

    private void mostrarInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.setHeaderText(null);
        a.show();
    }
}

