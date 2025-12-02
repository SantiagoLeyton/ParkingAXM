package com.example.parkingaxm.controllers;

import com.example.parkingaxm.enums.Rol;
import com.example.parkingaxm.models.Usuario;
import com.example.parkingaxm.services.LoginService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.example.parkingaxm.utils.SessionManager;

public class LoginViewController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;

    private final LoginService loginService = new LoginService();

    @FXML
    private void onLogin(ActionEvent event) {
        String user = txtUsuario.getText();
        String pass = txtPassword.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            mostrarAlerta("Campos Vacíos", "Debes ingresar usuario y contraseña.");
            return;
        }

        Usuario u = loginService.login(user, pass);

        if (u == null) {
            mostrarAlerta("Error", "Usuario o contraseña incorrectos.");
            return;
        }

        SessionManager.setUsuarioActual(u);

        if (u.getRol() == Rol.ADMIN) {
            cargarVista(event, "/com/example/parkingaxm/views/MenuAdmin.fxml");
        } else {
            cargarVista(event, "/com/example/parkingaxm/views/MenuOperario.fxml");
        }
    }

    private void cargarVista(ActionEvent event, String ruta) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error fatal", "No se pudo cargar la pantalla solicitada.");
        }
    }

    private void mostrarAlerta(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
