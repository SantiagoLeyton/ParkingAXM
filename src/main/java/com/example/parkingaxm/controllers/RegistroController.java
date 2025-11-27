package com.example.parkingaxm.controllers;

import com.example.parkingaxm.models.Registro;
import com.example.parkingaxm.services.ParqueaderoService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

/**
 * Controlador del módulo de registro de vehículos.
 * Se limita a delegar en ParqueaderoService y mostrar mensajes al usuario.
 */
public class RegistroController {

    private final ParqueaderoService parqueaderoService = new ParqueaderoService();

    /**
     * Acción que debería vincularse a un botón "Registrar entrada" en la vista.
     */
    @FXML
    private void onRegistrarEntrada(ActionEvent event) {
        try {
            Registro registro = parqueaderoService.registrarEntrada();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Entrada registrada");
            alert.setHeaderText("Vehículo ingresado correctamente");
            alert.setContentText(
                    "Placa: " + registro.getPlaca() + "\n" +
                    "Tipo: " + registro.getTipoVehiculo() + "\n" +
                    "Hora de entrada: " + registro.getEntrada()
            );
            alert.showAndWait();
        } catch (IllegalStateException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No se pudo registrar");
            alert.setHeaderText("No se registró la entrada");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error inesperado");
            alert.setHeaderText("Ocurrió un problema al registrar la entrada");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }
}
