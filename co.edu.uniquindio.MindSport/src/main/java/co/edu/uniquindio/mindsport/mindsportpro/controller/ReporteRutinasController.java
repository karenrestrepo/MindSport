package co.edu.uniquindio.mindsport.mindsportpro.controller;

import co.edu.uniquindio.mindsport.mindsportpro.dao.RutinaEstadisticaDAOJdbc;
import co.edu.uniquindio.mindsport.mindsportpro.model.RutinaEstadistica;
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
import java.util.List;

public class ReporteRutinasController {

    @FXML private TableView<RutinaEstadistica> tableRutinas;
    @FXML private TableColumn<RutinaEstadistica, Integer> tcId;
    @FXML private TableColumn<RutinaEstadistica, String> tcTitulo;
    @FXML private TableColumn<RutinaEstadistica, String> tcCoach;
    @FXML private TableColumn<RutinaEstadistica, Integer> tcSesiones;
    @FXML private TableColumn<RutinaEstadistica, Integer> tcAtletas;
    @FXML private TableColumn<RutinaEstadistica, Double> tcPuntuacion;
    @FXML private LineChart<Number, Number> lineRutinas;
    @FXML private NumberAxis ejeX;
    @FXML private NumberAxis ejeY;
    @FXML private Label lblMensaje;
    @FXML private Button btnVolver;
    @FXML private Button btnExportarPdf;

    private final ObservableList<RutinaEstadistica> datos = FXCollections.observableArrayList();
    private final RutinaEstadisticaDAOJdbc dao = RutinaEstadisticaDAOJdbc.getInstancia();

    @FXML
    void initialize() {
        configurarTabla();
        cargarDatos();
    }

    private void configurarTabla() {
        tcId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tcTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        tcCoach.setCellValueFactory(cell -> {
            RutinaEstadistica r = cell.getValue();
            String nombre = (r.getNombresCoach() != null ? r.getNombresCoach() : "") + " " +
                    (r.getApellidosCoach() != null ? r.getApellidosCoach() : "");
            return new javafx.beans.property.SimpleStringProperty(nombre.trim());
        });
        tcSesiones.setCellValueFactory(new PropertyValueFactory<>("sesionesRealizadas"));
        tcAtletas.setCellValueFactory(new PropertyValueFactory<>("atletasUnicos"));
        tcPuntuacion.setCellValueFactory(new PropertyValueFactory<>("puntuacionPromedio"));

        tableRutinas.setItems(datos);
    }

    private void cargarDatos() {
        // tomar datos base en lista mutable y evitar ConcurrentModification con subList sobre la misma observable
        var base = dao.listar();
        base.sort((a, b) -> Integer.compare(
                b.getSesionesRealizadas() == null ? 0 : b.getSesionesRealizadas(),
                a.getSesionesRealizadas() == null ? 0 : a.getSesionesRealizadas()));

        if (base.size() > 8) {
            base = new java.util.ArrayList<>(base.subList(0, 8));
        }

        datos.setAll(base);
        System.out.println("[ReporteRutinas] Registros cargados (top 8): " + datos.size());
        if (datos.isEmpty()) {
            lblMensaje.setText("No hay sesiones para calcular este reporte.");
            lineRutinas.getData().clear();
            return;
        }
        lblMensaje.setText("");
        poblarBarChart();
    }

    private void poblarBarChart() {
        lineRutinas.getData().clear();

        int maxSesiones = 0;
        XYChart.Series<Number, Number> serie = new XYChart.Series<>();
        serie.setName("Sesiones por rutina (Top)");
        for (RutinaEstadistica r : datos) {
            int idVal = r.getId() != null ? r.getId() : serie.getData().size() + 1;
            int sesiones = r.getSesionesRealizadas() != null ? r.getSesionesRealizadas() : 0;
            maxSesiones = Math.max(maxSesiones, sesiones);
            serie.getData().add(new XYChart.Data<>((Number) idVal, (Number) sesiones));
        }
        if (ejeX != null) {
            ejeX.setAutoRanging(false);
            ejeX.setLowerBound(0);
            ejeX.setUpperBound(Math.max(1, serie.getData().stream().mapToDouble(d -> d.getXValue().doubleValue()).max().orElse(1) + 1));
            ejeX.setTickUnit(1);
        }
        if (ejeY != null) {
            ejeY.setAutoRanging(false);
            int upper = Math.max(1, maxSesiones + 1);
            ejeY.setLowerBound(0);
            ejeY.setUpperBound(upper);
            ejeY.setTickUnit(Math.max(1, upper / 5.0));
        }
        lineRutinas.getData().add(serie);
        System.out.println("[ReporteRutinas] Barras dibujadas: " + serie.getData().size() + " (max sesiones=" + maxSesiones + ")");
    }

    @FXML
    void onExportarCsv(ActionEvent event) {
        try {
            String[] headers = {"ID", "Titulo", "Coach", "Sesiones", "Atletas", "Punt. Prom."};
            List<String[]> rows = new ArrayList<>();
            for (RutinaEstadistica r : datos) {
                String coach = ((r.getNombresCoach() != null ? r.getNombresCoach() : "") + " " + (r.getApellidosCoach() != null ? r.getApellidosCoach() : "")).trim();
                rows.add(new String[]{
                        safe(r.getId()),
                        r.getTitulo() != null ? r.getTitulo() : "",
                        coach,
                        safe(r.getSesionesRealizadas()),
                        safe(r.getAtletasUnicos()),
                        r.getPuntuacionPromedio() != null ? String.format("%.2f", r.getPuntuacionPromedio()) : "-"
                });
            }

            BufferedImage chartImg = null;
            if (lineRutinas != null) {
                chartImg = SwingFXUtils.fromFXImage(lineRutinas.snapshot(new SnapshotParameters(), null), null);
            }

            Path pdf = PdfExporter.exportar("Reporte Rutinas Mas Usadas", "reporte_rutinas.pdf", headers, rows, chartImg);
            lblMensaje.setText("PDF generado en: " + pdf.toAbsolutePath());
        } catch (Exception e) {
            lblMensaje.setText("Error al exportar PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String safe(Object o) {
        return o == null ? "-" : o.toString();
    }

    private String quote(String s) {
        if (s == null) return "-";
        return s;
    }

    @FXML
    void onVolver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uniquindio/mindsport/mindsportpro/MenuPrincipal.fxml"));
            Parent root = loader.load();

            Stage stage = SessionManager.getInstance().getStagePrincipal();
            Scene scene = new Scene(root, 700, 720);
            stage.setScene(scene);
            stage.setTitle("Menú Principal - MindSport Pro");
        } catch (IOException e) {
            System.err.println("Error al volver al menú: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
