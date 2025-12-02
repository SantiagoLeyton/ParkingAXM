package com.example.parkingaxm.controllers;

import com.example.parkingaxm.models.Usuario;
import com.example.parkingaxm.services.UsuarioService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;

public class UsuarioCrudController {

    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, String> colUsername;
    @FXML private TableColumn<Usuario, String> colRol;

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;

    @FXML private Button btnCrear;
    @FXML private Button btnActualizar;
    @FXML private Button btnCambiarPassword;
    @FXML private Button btnEliminar;
    @FXML private Button btnVolver;

    private final UsuarioService usuarioService = new UsuarioService();
    private final ObservableList<Usuario> modeloTabla = FXCollections.observableArrayList();

    // Se ejecuta al cargar el FXML
    @FXML
    public void initialize() {
        // Columnas
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRol.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getRol() != null
                                ? cellData.getValue().getRol().name()
                                : ""
                )
        );

        tablaUsuarios.setItems(modeloTabla);
        cargarUsuariosEnTabla();

        // Cuando seleccionas un usuario en la tabla, se pasa al formulario
        tablaUsuarios.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> {
                    if (newSel != null) {
                        txtUsername.setText(newSel.getUsername());
                        txtPassword.clear();
                    }
                }
        );
    }

    private void cargarUsuariosEnTabla() {
        modeloTabla.clear();
        List<Usuario> operarios = usuarioService.listarOperarios(); // Sólo operarios (no admin)
        modeloTabla.addAll(operarios);
    }

    private void limpiarFormulario() {
        txtUsername.clear();
        txtPassword.clear();
        tablaUsuarios.getSelectionModel().clearSelection();
    }

    // --------- CREAR USUARIO (OPERARIO) ---------
    @FXML
    private void onCrear(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        try {
            Usuario nuevo = usuarioService.crearOperario(username, password);
            mostrarInfo("Usuario creado", "Se creó el operario: " + nuevo.getUsername());
            limpiarFormulario();
            cargarUsuariosEnTabla();
        } catch (IllegalArgumentException e) {
            mostrarError("Datos inválidos", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error inesperado", e.getMessage());
        }
    }

    // --------- ACTUALIZAR USUARIO (username y opcionalmente contraseña) ---------
    @FXML
    private void onActualizar(ActionEvent event) {
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Sin selección", "Debes seleccionar un usuario de la tabla.");
            return;
        }

        String usernameActual = seleccionado.getUsername();
        String nuevoUsername = txtUsername.getText();
        String nuevaPassword = txtPassword.getText();

        if (nuevaPassword != null && nuevaPassword.isBlank()) {
            // si está vacío, no cambiamos la contraseña
            nuevaPassword = null;
        }

        try {
            usuarioService.editarOperario(usernameActual, nuevoUsername, nuevaPassword);
            mostrarInfo("Usuario actualizado", "Se actualizaron los datos del operario.");
            limpiarFormulario();
            cargarUsuariosEnTabla();
        } catch (IllegalArgumentException e) {
            mostrarError("No se pudo actualizar", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error inesperado", e.getMessage());
        }
    }

    // --------- CAMBIAR SOLO LA CONTRASEÑA ---------
    @FXML
    private void onCambiarPassword(ActionEvent event) {
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Sin selección", "Debes seleccionar un usuario de la tabla.");
            return;
        }

        String nuevaPassword = txtPassword.getText();
        if (nuevaPassword == null || nuevaPassword.isBlank()) {
            mostrarError("Contraseña vacía", "Ingresa la nueva contraseña.");
            return;
        }

        try {
            // username se mantiene, rol se mantiene, sólo cambia la contraseña
            usuarioService.editarOperario(seleccionado.getUsername(), null, nuevaPassword);
            mostrarInfo("Contraseña actualizada",
                    "Se cambió la contraseña de: " + seleccionado.getUsername());
            txtPassword.clear();
        } catch (IllegalArgumentException e) {
            mostrarError("No se pudo actualizar", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error inesperado", e.getMessage());
        }
    }

    // --------- ELIMINAR USUARIO ---------
    @FXML
    private void onEliminar(ActionEvent event) {
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Sin selección", "Debes seleccionar un usuario de la tabla.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Eliminar usuario");
        confirm.setContentText("¿Seguro que deseas eliminar al operario: "
                + seleccionado.getUsername() + "?");

        var result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                usuarioService.eliminarOperario(seleccionado.getUsername());
                mostrarInfo("Usuario eliminado", "Se eliminó el operario seleccionado.");
                limpiarFormulario();
                cargarUsuariosEnTabla();
            } catch (IllegalArgumentException e) {
                mostrarError("No se pudo eliminar", e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                mostrarError("Error inesperado", e.getMessage());
            }
        }
    }

    // --------- VOLVER AL MENÚ ADMIN ---------
    @FXML
    private void onVolver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/parkingaxm/views/MenuAdmin.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error de navegación", "No se pudo volver al menú.");
        }
    }

    // --------- Helpers de alertas ---------
    private void mostrarError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void mostrarInfo(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
