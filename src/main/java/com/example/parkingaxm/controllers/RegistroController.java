package com.example.parkingaxm.controllers;

import com.example.parkingaxm.enums.TipoVehiculo;
import com.example.parkingaxm.models.Registro;
import com.example.parkingaxm.services.ParqueaderoService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.regex.Pattern;

/**
 * Controlador de la pantalla de registro de vehículos.
 * - Datos manuales (placa y tipo).
 * - Validación básica de placa.
 * - Botón para volver al menú.
 */
public class RegistroController {

    private final ParqueaderoService parqueaderoService = new ParqueaderoService();

    @FXML
    private TextField txtPlaca;

    @FXML
    private ComboBox<TipoVehiculo> cmbTipoVehiculo;

    // Ejemplo de formato sencillo: 3 letras + 3 números (ABC123)
    private static final Pattern PLACA_PATTERN = Pattern.compile("^[A-Z]{3}[0-9]{3}$");

    @FXML
    public void initialize() {
        // Llenamos el combo con los valores del enum (CARRO, MOTO, etc.)
        cmbTipoVehiculo.getItems().setAll(TipoVehiculo.values());
    }

    /**
     * Registrar entrada usando los datos que el usuario escribió en la pantalla.
     */
    @FXML
    private void onRegistrarEntrada(ActionEvent event) {
        Registro registro = null;  // 🔹 Declarada aquí para usarla después del try

        try {
            String placa = txtPlaca.getText();
            TipoVehiculo tipo = cmbTipoVehiculo.getValue();

            if (placa == null || placa.trim().isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING,
                        "Datos incompletos",
                        "Debes ingresar la placa del vehículo.");
                return;
            }

            placa = placa.trim().toUpperCase();

            if (!PLACA_PATTERN.matcher(placa).matches()) {
                mostrarAlerta(Alert.AlertType.WARNING,
                        "Formato de placa inválido",
                        "La placa debe tener el formato ABC123 (3 letras y 3 números).");
                return;
            }

            if (tipo == null) {
                mostrarAlerta(Alert.AlertType.WARNING,
                        "Datos incompletos",
                        "Debes seleccionar el tipo de vehículo.");
                return;
            }

            // 🔹 Registrar vehículo
            registro = parqueaderoService.registrarEntradaManual(placa, tipo);

            // 🔹 Obtener espacios disponibles DESPUÉS del registro
            int disponibles = parqueaderoService.getEspaciosDisponibles();

            // 🔹 Una sola alerta consolidada
            mostrarAlerta(Alert.AlertType.INFORMATION,
                    "Entrada registrada",
                    "Vehículo ingresado correctamente:\n" +
                            "Placa: " + registro.getPlaca() + "\n" +
                            "Tipo: " + registro.getTipoVehiculo() + "\n" +
                            "Hora de entrada: " + registro.getEntrada() + "\n\n" +
                            "Espacios disponibles: " + disponibles
            );

            // 🔹 Limpiar campos
            txtPlaca.clear();
            cmbTipoVehiculo.getSelectionModel().clearSelection();

        } catch (IllegalStateException e) {
            mostrarAlerta(Alert.AlertType.WARNING,
                    "No se pudo registrar",
                    e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR,
                    "Error inesperado",
                    "Ocurrió un problema al registrar la entrada: " + e.getMessage());
        }
    }

    /**
     * Botón para volver al menú desde la pantalla de registro.
     */
    @FXML
    private void onVolverAlMenu(ActionEvent event) {
        String rutaMenu = MenuController.menuActualFXML;

        // Si por alguna razón no se ha definido, volvemos al login.
        if (rutaMenu == null || rutaMenu.isEmpty()) {
            rutaMenu = "/com/example/parkingaxm/views/LoginView.fxml";
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaMenu));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR,
                    "Error al volver",
                    "No se pudo volver al menú: " + e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String header, String content) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Registro de vehículos");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
