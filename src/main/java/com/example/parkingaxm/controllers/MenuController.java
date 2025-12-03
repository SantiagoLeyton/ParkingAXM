package com.example.parkingaxm.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;


public class MenuController {

    public static final String MENU_ADMIN_FXML = "/com/example/parkingaxm/views/MenuAdmin.fxml";
    public static final String MENU_OPERARIO_FXML = "/com/example/parkingaxm/views/MenuOperario.fxml";

    public static String menuActualFXML;

    private void cambiarVista(javafx.event.Event event, String rutaFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene().getWindow();

            stage.setScene(scene);

            // 🔥 FORZAR PANTALLA COMPLETA REAL
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    // ================== ADMIN ==================

    @FXML
    private void onRegistrarVehiculo(MouseEvent event) {
        menuActualFXML = MENU_ADMIN_FXML;
        cambiarVista(event, "/com/example/parkingaxm/views/RegistroView.fxml");
    }

    @FXML
    private void onListarVehiculos(MouseEvent event) {
        menuActualFXML = MENU_ADMIN_FXML;
        cambiarVista(event, "/com/example/parkingaxm/views/ListarView.fxml");
    }

    @FXML
    private void onCobro(MouseEvent event) {
        menuActualFXML = MENU_ADMIN_FXML;
        cambiarVista(event, "/com/example/parkingaxm/views/CobroView.fxml");
    }

    @FXML
    private void onEstadisticas(MouseEvent event) {
        menuActualFXML = MENU_ADMIN_FXML;
        cambiarVista(event, "/com/example/parkingaxm/views/EstadisticasView.fxml");
    }

    @FXML
    private void onCrearUsuario(MouseEvent event) {
        menuActualFXML = MENU_ADMIN_FXML;
        cambiarVista(event, "/com/example/parkingaxm/views/CrearUsuarioView.fxml");
    }

    @FXML
    private void onSalir(MouseEvent event) {
        cambiarVista(event, "/com/example/parkingaxm/views/LoginView.fxml");
    }

    // ================== OPERARIO ==================

    @FXML
    private void onRegistrarOperario(MouseEvent event) {
        menuActualFXML = MENU_OPERARIO_FXML;
        cambiarVista(event, "/com/example/parkingaxm/views/RegistroView.fxml");
    }

    @FXML
    private void onListarOperario(MouseEvent event) {
        menuActualFXML = MENU_OPERARIO_FXML;
        cambiarVista(event, "/com/example/parkingaxm/views/ListarView.fxml");
    }

    @FXML
    private void onCobroOperario(MouseEvent event) {
        menuActualFXML = MENU_OPERARIO_FXML;
        cambiarVista(event, "/com/example/parkingaxm/views/CobroView.fxml");
    }

    @FXML
    private void onSalirOperario(MouseEvent event) {
        cambiarVista(event, "/com/example/parkingaxm/views/LoginView.fxml");
    }
}
