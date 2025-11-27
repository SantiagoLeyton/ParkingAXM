package com.example.parkingaxm.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MenuController {

    // ================== NAVEGACIÓN COMÚN ==================

    private void cambiarVista(ActionEvent event, String rutaFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================== ACCIONES DEL MENÚ ADMIN ==================

    @FXML
    private void onRegistrarVehiculo(ActionEvent event) {
        cambiarVista(event, "/com/example/parkingaxm/views/RegistroView.fxml");
    }

    @FXML
    private void onListarVehiculos(ActionEvent event) {
        cambiarVista(event, "/com/example/parkingaxm/views/ListarView.fxml");
    }

    @FXML
    private void onCobro(ActionEvent event) {
        cambiarVista(event, "/com/example/parkingaxm/views/CobroView.fxml");
    }

    @FXML
    private void onEstadisticas(ActionEvent event) {
        cambiarVista(event, "/com/example/parkingaxm/views/EstadisticasView.fxml");
    }

    @FXML
    private void onCrearUsuario(ActionEvent event) {
        cambiarVista(event, "/com/example/parkingaxm/views/CrearUsuarioView.fxml");
    }

    @FXML
    private void onSalir(ActionEvent event) {
        cambiarVista(event, "/com/example/parkingaxm/views/LoginView.fxml");
    }

    // ================== ACCIONES DEL MENÚ OPERARIO ==================
    // (Solo las que se permiten)

    @FXML
    private void onRegistrarOperario(ActionEvent event) {
        onRegistrarVehiculo(event);
    }

    @FXML
    private void onListarOperario(ActionEvent event) {
        onListarVehiculos(event);
    }

    @FXML
    private void onCobroOperario(ActionEvent event) {
        onCobro(event);
    }

    @FXML
    private void onSalirOperario(ActionEvent event) {
        onSalir(event);
    }
}
