package com.example.parkingaxm.controllers;

import com.example.parkingaxm.enums.Rol;
import com.example.parkingaxm.enums.TipoVehiculo;
import com.example.parkingaxm.models.Registro;
import com.example.parkingaxm.services.ParqueaderoService;
import com.example.parkingaxm.utils.FileManager;
import com.example.parkingaxm.utils.SessionManager;
import com.example.parkingaxm.utils.TimeUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.List;

public class CobroController {

    @FXML
    private TextField txtPlaca;

    @FXML
    private Label lblHoras;

    @FXML
    private Label lblTotal;

    @FXML
    private Label lblEspacios;

    @FXML
    public void initialize() {
        ParqueaderoService ps = new ParqueaderoService();
        int disponibles = ps.getEspaciosDisponibles();
        lblEspacios.setText("Espacios disponibles: " + disponibles);
    }

    @FXML
    private void onCalcularCobro() {

        String placa = txtPlaca.getText().trim().toUpperCase();

        if (placa.isEmpty()) {
            mostrarError("Debe ingresar una placa.");
            return;
        }

        // Cargar registros desde el JSON
        List<Registro> registros = FileManager.cargarRegistros();

        // Buscar registro activo (sin salida) para esa placa
        Registro reg = registros.stream()
                .filter(r -> r.getPlaca().equalsIgnoreCase(placa))
                .filter(r -> r.getSalida() == null)
                .findFirst()
                .orElse(null);

        if (reg == null) {
            mostrarError("No existe un registro activo para esa placa.");
            limpiarCampos();
            return;
        }

        TipoVehiculo tipo = reg.getTipoVehiculo();
        if (tipo == null) {
            mostrarError("El registro no tiene un tipo de vehículo asignado.");
            return;
        }

        // Hora actual = salida
        LocalDateTime salida = LocalDateTime.now();

        long minutos = TimeUtils.calcularMinutos(reg.getEntrada(), salida);
        long lapsos = TimeUtils.calcularLapsos30(minutos);
        double total = TimeUtils.calcularTotal(tipo, lapsos);

        // Actualizar el registro
        reg.setSalida(salida);
        reg.setTotal(total);

        // Guardar cambios en registros.json
        FileManager.guardarRegistros(registros);

        // Liberar espacio
        ParqueaderoService ps = new ParqueaderoService();
        ps.liberarEspacio();

        // Actualizar espacios disponibles en pantalla
        int disponiblesActualizados = ps.getEspaciosDisponibles();
        lblEspacios.setText("Espacios disponibles: " + disponiblesActualizados);

        // Mostrar info al usuario
        mostrarInfo(
                "Cobro realizado.\n\n" +
                        "Placa: " + reg.getPlaca() + "\n" +
                        "Tipo: " + tipo + "\n" +
                        "Minutos: " + minutos + "\n" +
                        "Lapsos de 30 min: " + lapsos + "\n" +
                        "Total: $" + (long) total + "\n\n" +
                        "Espacios disponibles: " + disponiblesActualizados
        );


        txtPlaca.clear();
    }

    @FXML
    private void onVolverMenu(ActionEvent event) {
        Rol rol = SessionManager.getRolActual();
        String ruta;

        if (rol == Rol.ADMIN) {
            ruta = "/com/example/parkingaxm/views/MenuAdmin.fxml";
        } else {
            ruta = "/com/example/parkingaxm/views/MenuOperario.fxml";
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("No se pudo volver al menú.");
        }
    }

    // ================== Helpers ==================

    private void limpiarCampos() {
        lblHoras.setText("Horas:");
        lblTotal.setText("Total a pagar:");
    }

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
