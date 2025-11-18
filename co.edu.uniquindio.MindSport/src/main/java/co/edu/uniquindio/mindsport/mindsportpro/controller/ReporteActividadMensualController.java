package co.edu.uniquindio.mindsport.mindsportpro.controller;

import co.edu.uniquindio.mindsport.mindsportpro.dao.ActividadMensualDAOJdbc;
import co.edu.uniquindio.mindsport.mindsportpro.model.ActividadMensual;
import co.edu.uniquindio.mindsport.mindsportpro.util.PdfExporter;
import co.edu.uniquindio.mindsport.mindsportpro.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ReporteActividadMensualController {

    @FXML private TableView<ActividadMensual> tableActividad;
    @FXML private TableColumn<ActividadMensual, String> tcPeriodo;
    @FXML private TableColumn<ActividadMensual, Integer> tcTotalSesiones;
    @FXML private TableColumn<ActividadMensual, Integer> tcAtletas;
    @FXML private TableColumn<ActividadMensual, Integer> tcRutinas;
    @FXML private TableColumn<ActividadMensual, Double> tcPuntuacion;
    @FXML private TableColumn<ActividadMensual, Double> tcDuracionProm;
    @FXML private TableColumn<ActividadMensual, Integer> tcTiempoTotal;
    @FXML private LineChart<Number, Number> lineSesiones;
    @FXML private NumberAxis ejeX;
    @FXML private NumberAxis ejeY;
    @FXML private Label lblMensaje;
    @FXML private Button btnVolver;
    @FXML private Button btnExportarPdf;

    private final ActividadMensualDAOJdbc dao = ActividadMensualDAOJdbc.getInstancia();
    private final ObservableList<ActividadMensual> datos = FXCollections.observableArrayList();

    @FXML
    void initialize() {
        configurarTabla();
        cargarDatos();
    }

    private void configurarTabla() {
        tcPeriodo.setCellValueFactory(new PropertyValueFactory<>("periodo"));
        tcTotalSesiones.setCellValueFactory(new PropertyValueFactory<>("totalSesiones"));
        tcAtletas.setCellValueFactory(new PropertyValueFactory<>("atletasActivos"));
        tcRutinas.setCellValueFactory(new PropertyValueFactory<>("rutinasUtilizadas"));
        tcPuntuacion.setCellValueFactory(new PropertyValueFactory<>("puntuacionPromedio"));
        tcDuracionProm.setCellValueFactory(new PropertyValueFactory<>("duracionPromedioMin"));
        tcTiempoTotal.setCellValueFactory(new PropertyValueFactory<>("tiempoTotalMin"));
        tableActividad.setItems(datos);
    }

    private void cargarDatos() {
        List<ActividadMensual> lista;
        try {
            lista = dao.listar();
        } catch (Exception e) {
            e.printStackTrace();
            lblMensaje.setText("Error al cargar datos: " + e.getMessage());
            return;
        }
        if (lista == null) lista = new ArrayList<>();

        lista.sort(Comparator.comparing(ActividadMensual::getYearMonth, Comparator.nullsLast(Comparator.naturalOrder())));
        datos.setAll(lista);

        if (lista.isEmpty()) {
            lblMensaje.setText("No hay sesiones en los ultimos 12 meses.");
            if (lineSesiones != null) lineSesiones.getData().clear();
            return;
        }
        lblMensaje.setText("");
        poblarLinea(lista);
    }

    private void poblarLinea(List<ActividadMensual> lista) {
        if (lineSesiones == null) {
            System.err.println("[ReporteActividadMensual] lineSesiones es null, revisa fx:id en el FXML.");
            return;
        }
        lineSesiones.getData().clear();
        XYChart.Series<Number, Number> serie = new XYChart.Series<>();
        serie.setName("Sesiones por mes");

        int max = 0;
        int index = 1;
        for (ActividadMensual a : lista) {
            int sesiones = a.getTotalSesiones() != null ? a.getTotalSesiones() : 0;
            max = Math.max(max, sesiones);
            XYChart.Data<Number, Number> punto = new XYChart.Data<>(index++, sesiones);
            punto.setExtraValue(a.getPeriodo());
            serie.getData().add(punto);
        }

        if (ejeX != null) {
            ejeX.setAutoRanging(false);
            ejeX.setLowerBound(1);
            ejeX.setUpperBound(lista.size());
            ejeX.setTickUnit(1);
            ejeX.setLabel("Mes (orden cronologico)");
        }
        if (ejeY != null) {
            ejeY.setAutoRanging(false);
            int upper = Math.max(1, max + 1);
            ejeY.setLowerBound(0);
            ejeY.setUpperBound(upper);
            ejeY.setTickUnit(Math.max(1, upper / 6.0));
            ejeY.setLabel("Total de sesiones");
        }
        lineSesiones.getData().add(serie);
    }

    @FXML
    void onExportarPdf(ActionEvent event) {
        try {
            String[] headers = {"Periodo", "Sesiones", "Atletas", "Rutinas", "Punt. Prom", "Dur. Prom", "Tiempo Total"};
            List<String[]> rows = new ArrayList<>();
            for (ActividadMensual a : datos) {
                rows.add(new String[]{
                        safe(a.getPeriodo()),
                        safe(a.getTotalSesiones()),
                        safe(a.getAtletasActivos()),
                        safe(a.getRutinasUtilizadas()),
                        a.getPuntuacionPromedio() != null ? String.format("%.2f", a.getPuntuacionPromedio()) : "-",
                        a.getDuracionPromedioMin() != null ? String.format("%.2f", a.getDuracionPromedioMin()) : "-",
                        safe(a.getTiempoTotalMin())
                });
            }

            BufferedImage chartImg = null;
            if (lineSesiones != null) {
                chartImg = SwingFXUtils.fromFXImage(lineSesiones.snapshot(new SnapshotParameters(), null), null);
            }

            Path pdf = PdfExporter.exportar("Actividad Mensual (12 meses)", "reporte_actividad_mensual.pdf", headers, rows, chartImg);
            lblMensaje.setText("PDF generado en: " + pdf.toAbsolutePath());
        } catch (Exception e) {
            lblMensaje.setText("Error al exportar PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String safe(Object n) {
        return n == null ? "-" : n.toString();
    }

    @FXML
    void onVolver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uniquindio/mindsport/mindsportpro/MenuPrincipal.fxml"));
            Parent root = loader.load();

            Stage stage = SessionManager.getInstance().getStagePrincipal();
            Scene scene = new Scene(root, 700, 720);
            stage.setScene(scene);
            stage.setTitle("Menu Principal - MindSport Pro");
        } catch (IOException e) {
            System.err.println("Error al volver al menu: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
