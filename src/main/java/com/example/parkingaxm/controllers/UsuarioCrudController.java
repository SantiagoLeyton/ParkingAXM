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

    @FXML
    public void initialize() {
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
        List<Usuario> operarios = usuarioService.listarOperarios();
        modeloTabla.addAll(operarios);
    }

    private void limpiarFormulario() {
        txtUsername.clear();
        txtPassword.clear();
        tablaUsuarios.getSelectionModel().clearSelection();
    }

    private boolean validarOperarioSeleccionado(Usuario seleccionado) {
        if (seleccionado == null) {
            mostrarError("Sin selección", "Debes seleccionar un usuario.");
            return false;
        }

        if (seleccionado.getRol() != com.example.parkingaxm.enums.Rol.OPERARIO) {
            mostrarError("Operación inválida", "Solo puedes gestionar usuarios tipo OPERARIO.");
            return false;
        }

        return true;
    }

    // --------- CREAR USUARIO ---------
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

    // --------- ACTUALIZAR USUARIO ---------
    @FXML
    private void onActualizar(ActionEvent event) {
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();

        if (!validarOperarioSeleccionado(seleccionado)) return;

        String usernameActual = seleccionado.getUsername();

        // Si el username está vacío, mantener el actual
        String nuevoUsername = txtUsername.getText().isBlank()
                ? usernameActual
                : txtUsername.getText();

        // Si password está vacío -> no cambiarla
        String nuevaPassword = txtPassword.getText().isBlank()
                ? null
                : txtPassword.getText();

        try {
            usuarioService.editarOperario(usernameActual, nuevoUsername, nuevaPassword);
            mostrarInfo("Usuario actualizado", "Se actualizaron los datos del operario.");
            limpiarFormulario();
            cargarUsuariosEnTabla();
        } catch (IllegalArgumentException e) {
            mostrarError("Error al actualizar", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error inesperado", e.getMessage());
        }
    }

    // --------- CAMBIAR SOLO LA CONTRASEÑA ---------
    @FXML
    private void onCambiarPassword(ActionEvent event) {
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();

        if (!validarOperarioSeleccionado(seleccionado)) return;

        String nuevaPassword = txtPassword.getText();

        if (nuevaPassword.isBlank()) {
            mostrarError("Contraseña vacía", "Ingresa una nueva contraseña.");
            return;
        }

        try {
            usuarioService.editarOperario(seleccionado.getUsername(), null, nuevaPassword);

            mostrarInfo("Contraseña actualizada",
                    "Se cambió la contraseña del usuario: " + seleccionado.getUsername());

            txtPassword.clear();
            cargarUsuariosEnTabla();
        } catch (IllegalArgumentException e) {
            mostrarError("Error al actualizar", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error inesperado", e.getMessage());
        }
    }

    // --------- ELIMINAR USUARIO ---------
    @FXML
    private void onEliminar(ActionEvent event) {
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();

        if (!validarOperarioSeleccionado(seleccionado)) return;

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

    // --------- VOLVER ---------
    @FXML
    private void onVolver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/parkingaxm/views/MenuAdmin.fxml"));

            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(scene);
            stage.setMaximized(true); // <-- ahora no vuelve a una esquina
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error de navegación", "No se pudo volver al menú.");
        }
    }

    // --------- ALERTAS ---------
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
