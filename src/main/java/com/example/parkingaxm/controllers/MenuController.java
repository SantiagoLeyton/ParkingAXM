package com.example.parkingaxm.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Controlador de los menús de Admin y Operario.
 * Desde aquí se navega a las diferentes pantallas del sistema.
 */
public class MenuController {

    public static final String MENU_ADMIN_FXML = "/com/example/parkingaxm/views/MenuAdmin.fxml";
    public static final String MENU_OPERARIO_FXML = "/com/example/parkingaxm/views/MenuOperario.fxml";

    /**
     * Ruta del menú desde el que se salió hacia una pantalla hija
     * (registro, listar, cobro...). Se usa para el botón "Volver".
     */
    public static String menuActualFXML;

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
        menuActualFXML = MENU_ADMIN_FXML;
        cambiarVista(event, "/com/example/parkingaxm/views/RegistroView.fxml");
    }

    @FXML
    private void onListarVehiculos(ActionEvent event) {
        menuActualFXML = MENU_ADMIN_FXML;
        cambiarVista(event, "/com/example/parkingaxm/views/ListarView.fxml");
    }

    @FXML
    private void onCobro(ActionEvent event) {
        menuActualFXML = MENU_ADMIN_FXML;
        cambiarVista(event, "/com/example/parkingaxm/views/CobroView.fxml");
    }

    @FXML
    private void onEstadisticas(ActionEvent event) {
        menuActualFXML = MENU_ADMIN_FXML;
        cambiarVista(event, "/com/example/parkingaxm/views/EstadisticasView.fxml");
    }

    @FXML
    private void onCrearUsuario(ActionEvent event) {
        menuActualFXML = MENU_ADMIN_FXML;
        cambiarVista(event, "/com/example/parkingaxm/views/CrearUsuarioView.fxml");
    }

    @FXML
    private void onSalir(ActionEvent event) {
        cambiarVista(event, "/com/example/parkingaxm/views/LoginView.fxml");
    }

    // ================== ACCIONES DEL MENÚ OPERARIO ==================

    @FXML
    private void onRegistrarOperario(ActionEvent event) {
        menuActualFXML = MENU_OPERARIO_FXML;
        cambiarVista(event, "/com/example/parkingaxm/views/RegistroView.fxml");
    }

    @FXML
    private void onListarOperario(ActionEvent event) {
        menuActualFXML = MENU_OPERARIO_FXML;
        cambiarVista(event, "/com/example/parkingaxm/views/ListarView.fxml");
    }

    @FXML
    private void onCobroOperario(ActionEvent event) {
        menuActualFXML = MENU_OPERARIO_FXML;
        cambiarVista(event, "/com/example/parkingaxm/views/CobroView.fxml");
    }

    @FXML
    private void onSalirOperario(ActionEvent event) {
        cambiarVista(event, "/com/example/parkingaxm/views/LoginView.fxml");
    }
}
