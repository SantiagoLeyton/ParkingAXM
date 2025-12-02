package com.example.parkingaxm.controllers;

import com.example.parkingaxm.models.Registro;
import com.example.parkingaxm.utils.FileManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Controlador de la pantalla que lista los vehículos registrados.
 */
public class ListarController {

    @FXML
    private TableView<Registro> tablaVehiculos;

    @FXML
    private TableColumn<Registro, String> colPlaca;

    @FXML
    private TableColumn<Registro, String> colTipo;

    @FXML
    private TableColumn<Registro, String> colEntrada;

    @FXML
    private TableColumn<Registro, String> colSalida;

    @FXML
    private TableColumn<Registro, String> colTotal;

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final NumberFormat FORMATO_MONEDA =
            NumberFormat.getCurrencyInstance(new Locale("es", "CO"));

    @FXML
    public void initialize() {
        // Configurar las columnas para mostrar los datos del Registro
        colPlaca.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPlaca()));

        colTipo.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getTipoVehiculo() != null
                                ? cellData.getValue().getTipoVehiculo().toString()
                                : "")
        );

        colEntrada.setCellValueFactory(cellData ->
                new SimpleStringProperty(formatearFecha(cellData.getValue().getEntrada())));

        colSalida.setCellValueFactory(cellData ->
                new SimpleStringProperty(formatearFecha(cellData.getValue().getSalida())));

        colTotal.setCellValueFactory(cellData ->
                new SimpleStringProperty(formatearTotal(cellData.getValue().getTotal())));

        cargarRegistros();
    }

    private String formatearFecha(LocalDateTime fecha) {
        return fecha != null ? fecha.format(FORMATO_FECHA) : "";
    }

    private String formatearTotal(double total) {
        if (total <= 0) {
            return "";
        }
        return FORMATO_MONEDA.format(total);
    }

    private void cargarRegistros() {
        List<Registro> registros = FileManager.cargarRegistros();
        ObservableList<Registro> data = FXCollections.observableArrayList(registros);
        tablaVehiculos.setItems(data);
    }

    /**
     * Botón para volver al menú (admin u operario) usando la ruta guardada en MenuController.
     */
    @FXML
    private void onVolver(ActionEvent event) {
        String rutaMenu = MenuController.menuActualFXML;

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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error al volver");
            alert.setContentText("No se pudo volver al menú: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
