package co.edu.uniquindio.mindsport.mindsportpro.controller;

import co.edu.uniquindio.mindsport.mindsportpro.dao.RutinaDetalleDAOJdbc;
import co.edu.uniquindio.mindsport.mindsportpro.model.RutinaDetalle;
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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
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

public class ReporteRutinasDetalleController {

    @FXML private TableView<RutinaDetalle> tableDetalle;
    @FXML private TableColumn<RutinaDetalle, Integer> tcId;
    @FXML private TableColumn<RutinaDetalle, String> tcTitulo;
    @FXML private TableColumn<RutinaDetalle, String> tcNivel;
    @FXML private TableColumn<RutinaDetalle, Integer> tcDuracionEstimada;
    @FXML private TableColumn<RutinaDetalle, String> tcCoach;
    @FXML private TableColumn<RutinaDetalle, Integer> tcNumEjercicios;
    @FXML private TableColumn<RutinaDetalle, Integer> tcDuracionTotal;
    @FXML private TableColumn<RutinaDetalle, Double> tcDuracionProm;
    @FXML private TableColumn<RutinaDetalle, Integer> tcFisicos;
    @FXML private TableColumn<RutinaDetalle, Integer> tcMentales;
    @FXML private TableColumn<RutinaDetalle, Integer> tcMixtos;
    @FXML private TableColumn<RutinaDetalle, String> tcFases;
    @FXML private Label lblMensaje;
    @FXML private Button btnVolver;
    @FXML private BarChart<Number, String> barDuraciones;
    @FXML private NumberAxis ejeDurX;
    @FXML private CategoryAxis ejeDurY;
    @FXML private Button btnExportarPdf;

    private final ObservableList<RutinaDetalle> datos = FXCollections.observableArrayList();
    private final RutinaDetalleDAOJdbc dao = RutinaDetalleDAOJdbc.getInstancia();

    @FXML
    void initialize() {
        configurarTabla();
        cargarDatos();
    }

    private void configurarTabla() {
        tcId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tcTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        tcNivel.setCellValueFactory(new PropertyValueFactory<>("nivelDificultadTexto"));
        tcDuracionEstimada.setCellValueFactory(new PropertyValueFactory<>("duracionEstimada"));
        tcCoach.setCellValueFactory(cell -> {
            RutinaDetalle r = cell.getValue();
            String nombre = (r.getNombresCoach() != null ? r.getNombresCoach() : "") + " " +
                    (r.getApellidosCoach() != null ? r.getApellidosCoach() : "");
            return new javafx.beans.property.SimpleStringProperty(nombre.trim());
        });
        tcNumEjercicios.setCellValueFactory(new PropertyValueFactory<>("numeroEjercicios"));
        tcDuracionTotal.setCellValueFactory(new PropertyValueFactory<>("duracionTotalEjercicios"));
        tcDuracionProm.setCellValueFactory(new PropertyValueFactory<>("duracionPromedioEjercicio"));
        tcFisicos.setCellValueFactory(new PropertyValueFactory<>("ejerciciosFisicos"));
        tcMentales.setCellValueFactory(new PropertyValueFactory<>("ejerciciosMentales"));
        tcMixtos.setCellValueFactory(new PropertyValueFactory<>("ejerciciosMixtos"));
        tcFases.setCellValueFactory(new PropertyValueFactory<>("fasesIncluidas"));
        tableDetalle.setItems(datos);
    }

    private void cargarDatos() {
        datos.setAll(dao.listar());
        if (datos.isEmpty()) {
            lblMensaje.setText("No hay rutinas con ejercicios para mostrar.");
            if (barDuraciones != null) {
                barDuraciones.getData().clear();
            }
        } else {
            lblMensaje.setText("");
            poblarGraficaDuracion();
        }
    }

    private void poblarGraficaDuracion() {
        if (barDuraciones == null) return;
        barDuraciones.getData().clear();
        if (ejeDurY != null) ejeDurY.getCategories().clear();

        var lista = new ArrayList<>(datos);
        lista.sort((a, b) -> Integer.compare(
                (b.getDuracionTotalEjercicios() != null ? b.getDuracionTotalEjercicios() : (b.getDuracionEstimada() != null ? b.getDuracionEstimada() : 0)),
                (a.getDuracionTotalEjercicios() != null ? a.getDuracionTotalEjercicios() : (a.getDuracionEstimada() != null ? a.getDuracionEstimada() : 0))
        ));
        if (lista.size() > 8) lista = new ArrayList<>(lista.subList(0, 8));

        XYChart.Series<Number, String> serie = new XYChart.Series<>();
        serie.setName("Duracion total por rutina");
        int max = 0;
        for (RutinaDetalle r : lista) {
            int dur = r.getDuracionTotalEjercicios() != null ? r.getDuracionTotalEjercicios()
                    : (r.getDuracionEstimada() != null ? r.getDuracionEstimada() : 0);
            max = Math.max(max, dur);
            String cat = "ID " + (r.getId() != null ? r.getId() : "-");
            serie.getData().add(new XYChart.Data<>((Number) dur, cat));
            if (ejeDurY != null) ejeDurY.getCategories().add(cat);
        }

        if (ejeDurX != null) {
            ejeDurX.setAutoRanging(false);
            int upper = Math.max(1, max + 5);
            ejeDurX.setLowerBound(0);
            ejeDurX.setUpperBound(upper);
            ejeDurX.setTickUnit(Math.max(1, upper / 5.0));
        }
        barDuraciones.getData().add(serie);
    }

    @FXML
    void onExportarPdf(ActionEvent event) {
        try {
            String[] headers = {"ID", "Titulo", "Nivel", "Dur Est.", "Coach", "#Ejercicios", "Dur Total", "Dur Prom", "Fisicos", "Mentales", "Mixtos", "Fases"};
            List<String[]> rows = new ArrayList<>();
            for (RutinaDetalle r : datos) {
                String coach = ((r.getNombresCoach() != null ? r.getNombresCoach() : "") + " " + (r.getApellidosCoach() != null ? r.getApellidosCoach() : "")).trim();
                rows.add(new String[]{
                        safe(r.getId()),
                        r.getTitulo() != null ? r.getTitulo() : "",
                        r.getNivelDificultadTexto() != null ? r.getNivelDificultadTexto() : "",
                        safe(r.getDuracionEstimada()),
                        coach,
                        safe(r.getNumeroEjercicios()),
                        safe(r.getDuracionTotalEjercicios()),
                        r.getDuracionPromedioEjercicio() != null ? String.format("%.2f", r.getDuracionPromedioEjercicio()) : "-",
                        safe(r.getEjerciciosFisicos()),
                        safe(r.getEjerciciosMentales()),
                        safe(r.getEjerciciosMixtos()),
                        r.getFasesIncluidas() != null ? r.getFasesIncluidas() : ""
                });
            }

            BufferedImage chartImg = null;
            if (barDuraciones != null) {
                chartImg = SwingFXUtils.fromFXImage(barDuraciones.snapshot(new javafx.scene.SnapshotParameters(), null), null);
            }

            Path pdf = PdfExporter.exportar("Composicion Detallada de Rutinas", "reporte_rutinas_detalle.pdf", headers, rows, chartImg);
            lblMensaje.setText("PDF generado en: " + pdf.toAbsolutePath());
        } catch (Exception e) {
            lblMensaje.setText("Error al exportar PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String safe(Object o) {
        return o == null ? "-" : o.toString();
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
