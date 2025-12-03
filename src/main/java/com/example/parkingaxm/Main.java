package com.example.parkingaxm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/LoginView.fxml"));
        Scene scene = new Scene(loader.load());

        // Icono de la aplicación
        Image icon = new Image(
                getClass().getResourceAsStream("/com/example/parkingaxm/img/logo.png")
        );
        stage.getIcons().add(icon);

        stage.setScene(scene);
        stage.setTitle("ParkingAXM");

        // 🔥 FORZAR TAMÑO DE VENTANA A TODO EL MONITOR
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
