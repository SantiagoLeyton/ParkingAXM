package com.example.parkingaxm.controllers;

import com.example.parkingaxm.enums.TipoVehiculo;
import com.example.parkingaxm.services.EstadisticasService;
import com.example.parkingaxm.services.EstadisticasService.EstadisticasDTO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.FileOutputStream;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class EstadisticasController implements Initializable {

    @FXML private DatePicker dpDesde;
    @FXML private DatePicker dpHasta;
    @FXML private ComboBox<String> cbTipo;

    @FXML private Label lblTotal;
    @FXML private Label lblCarros;
    @FXML private Label lblMotos;

    @FXML private Label lblHoy;
    @FXML private Label lblSemana;
    @FXML private Label lblMes;
    @FXML private Label lblDineroHoy;
    @FXML private Label lblDineroSemana;
    @FXML private Label lblDineroMes;
    @FXML private Label lblDineroTotal;
    @FXML private Label lblPermanenciaProm;
    @FXML private Label lblPermanenciaMax;

    @FXML private PieChart pieTipos;
    @FXML private BarChart<String, Number> barVehiculosDia;
    @FXML private BarChart<String, Number> barDineroDia;
    @FXML private GridPane gridHeatmap;

    private final EstadisticasService service = new EstadisticasService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cbTipo.getItems().addAll("Todos", "Carro", "Moto");
        cbTipo.getSelectionModel().selectFirst();
        cargarDatos();
    }

    @FXML
    private void onAplicarFiltros() {
        cargarDatos();
    }

    @FXML
    private void onExportarPdf() {
        try {
            LocalDate desde = dpDesde.getValue();
            LocalDate hasta = dpHasta.getValue();
            TipoVehiculo tipo = obtenerTipoSeleccionado();
            EstadisticasDTO data = service.obtenerEstadisticas(desde, hasta, tipo);

            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream("estadisticas_parkingAXM.pdf"));
            doc.open();

            doc.add(new Paragraph("Estadísticas ParkingAXM"));
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Total vehículos: " + data.totalVehiculos));
            doc.add(new Paragraph("Carros: " + data.totalCarros));
            doc.add(new Paragraph("Motos: " + data.totalMotos));
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Vehículos hoy: " + (data.carrosHoy + data.motosHoy)));
            doc.add(new Paragraph("Vehículos semana: " + (data.carrosSemana + data.motosSemana)));
            doc.add(new Paragraph("Vehículos mes: " + (data.carrosMes + data.motosMes)));
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Dinero hoy: " + data.dineroHoy));
            doc.add(new Paragraph("Dinero semana: " + data.dineroSemana));
            doc.add(new Paragraph("Dinero mes: " + data.dineroMes));
            doc.add(new Paragraph("Dinero total: " + data.dineroTotal));
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Permanencia promedio (min): " + String.format("%.1f", data.permanenciaPromedioMin)));
            doc.add(new Paragraph("Permanencia máxima (min): " + data.permanenciaMaxMin));

            doc.close();

            mostrarAlerta("PDF generado",
                    "Se creó el archivo estadisticas_parkingAXM.pdf en la carpeta del proyecto.");

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("No se pudo generar el PDF: " + e.getMessage());
        }
    }

    private void cargarDatos() {
        LocalDate desde = dpDesde.getValue();
        LocalDate hasta = dpHasta.getValue();
        TipoVehiculo tipo = obtenerTipoSeleccionado();

        EstadisticasDTO data = service.obtenerEstadisticas(desde, hasta, tipo);

        // Resumen
        lblTotal.setText(String.valueOf(data.totalVehiculos));
        lblCarros.setText(String.valueOf(data.totalCarros));
        lblMotos.setText(String.valueOf(data.totalMotos));

        lblHoy.setText(String.valueOf(data.carrosHoy + data.motosHoy));
        lblSemana.setText(String.valueOf(data.carrosSemana + data.motosSemana));
        lblMes.setText(String.valueOf(data.carrosMes + data.motosMes));

        lblDineroHoy.setText(String.format("$%,.0f", data.dineroHoy));
        lblDineroSemana.setText(String.format("$%,.0f", data.dineroSemana));
        lblDineroMes.setText(String.format("$%,.0f", data.dineroMes));
        lblDineroTotal.setText(String.format("$%,.0f", data.dineroTotal));

        lblPermanenciaProm.setText(String.format("%.1f min", data.permanenciaPromedioMin));
        lblPermanenciaMax.setText(data.permanenciaMaxMin + " min");

        // Pie chart
        pieTipos.getData().clear();
        if (data.totalCarros > 0) pieTipos.getData().add(new PieChart.Data("Carros", data.totalCarros));
        if (data.totalMotos > 0) pieTipos.getData().add(new PieChart.Data("Motos", data.totalMotos));

        // Barras vehículos por día
        barVehiculosDia.getData().clear();
        XYChart.Series<String, Number> serieVehiculos = new XYChart.Series<>();
        serieVehiculos.setName("Vehículos");
        data.vehiculosPorDia.forEach((dia, cant) ->
                serieVehiculos.getData().add(new XYChart.Data<>(dia, cant)));
        barVehiculosDia.getData().add(serieVehiculos);

        // Barras dinero por día
        barDineroDia.getData().clear();
        XYChart.Series<String, Number> serieDinero = new XYChart.Series<>();
        serieDinero.setName("Recaudo");
        data.dineroPorDia.forEach((dia, monto) ->
                serieDinero.getData().add(new XYChart.Data<>(dia, monto)));
        barDineroDia.getData().add(serieDinero);

        // Heatmap
        construirHeatmap(data);
    }

    private TipoVehiculo obtenerTipoSeleccionado() {
        String sel = cbTipo.getSelectionModel().getSelectedItem();
        if ("Carro".equalsIgnoreCase(sel)) return TipoVehiculo.CARRO;
        if ("Moto".equalsIgnoreCase(sel)) return TipoVehiculo.MOTO;
        return null; // Todos
    }

    private void construirHeatmap(EstadisticasDTO data) {
        gridHeatmap.getChildren().clear();

        // Encabezados
        String[] horas = new String[24];
        for (int h = 0; h < 24; h++) horas[h] = String.valueOf(h);

        // Primera fila: horas
        for (int h = 0; h < 24; h++) {
            Label lbl = new Label(String.valueOf(h));
            lbl.setMinSize(25, 20);
            gridHeatmap.add(lbl, h + 1, 0);
        }

        DayOfWeek[] dias = DayOfWeek.values();
        for (int i = 0; i < dias.length; i++) {
            DayOfWeek dia = dias[i];
            Label lblDia = new Label(dia.toString().substring(0, 3));
            lblDia.setMinSize(30, 20);
            gridHeatmap.add(lblDia, 0, i + 1);

            for (int h = 0; h < 24; h++) {
                long count = 0;
                if (data.heatmap.containsKey(dia)) {
                    count = data.heatmap.get(dia).getOrDefault(h, 0L);
                }

                Rectangle rect = new Rectangle(25, 20);
                rect.setStroke(Color.LIGHTGRAY);
                rect.setFill(colorHeatmap(count));

                Tooltip tooltip = new Tooltip(
                        dia + " " + h + ":00 - " + count + " vehículos");
                Tooltip.install(rect, tooltip);

                gridHeatmap.add(rect, h + 1, i + 1);
            }
        }
    }

    private Color colorHeatmap(long count) {
        if (count == 0) return Color.web("#f0f0f0");
        if (count == 1) return Color.web("#d0e6ff");
        if (count == 2) return Color.web("#90c2ff");
        if (count <= 4) return Color.web("#4a90e2");
        return Color.web("#003d99");
    }

    private void mostrarAlerta(String header, String content) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(header);
        a.setContentText(content);
        a.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        a.showAndWait();
    }

    private void mostrarError(String content) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText("Error");
        a.setContentText(content);
        a.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        a.showAndWait();
    }
}
