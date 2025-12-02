package com.example.parkingaxm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Cargar la vista inicial (Login)
        URL fxmlUrl = getClass().getResource("/com/example/parkingaxm/views/LoginView.fxml");
        if (fxmlUrl == null) {
            System.out.println("[Main] ❌ NO se encontró /com/example/parkingaxm/views/LoginView.fxml");
            throw new IllegalStateException("No se encontró LoginView.fxml en resources");
        } else {
            System.out.println("[Main] ✅ LoginView FXML encontrado en: " + fxmlUrl);
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Scene scene = new Scene(loader.load());

        // Cargar CSS global
        URL cssUrl = getClass().getResource("/com/example/parkingaxm/css/estilos.css");
        if (cssUrl == null) {
            System.out.println("[Main] ⚠ NO se encontró /com/example/parkingaxm/css/estilos.css");
        } else {
            System.out.println("[Main] 🎨 CSS cargado desde: " + cssUrl);
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        stage.setScene(scene);
        stage.setTitle("ParkingAXM");
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
