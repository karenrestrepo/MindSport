package co.edu.uniquindio.mindsport.mindsportpro.controller;

import co.edu.uniquindio.mindsport.mindsportpro.dao.AtletaSuperiorPromedioDAOJdbc;
import co.edu.uniquindio.mindsport.mindsportpro.model.AtletaSuperiorPromedio;
import co.edu.uniquindio.mindsport.mindsportpro.util.PdfExporter;
import co.edu.uniquindio.mindsport.mindsportpro.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ReporteAtletasSuperiorController {

    @FXML private TableView<AtletaSuperiorPromedio> tableAtletas;
    @FXML private TableColumn<AtletaSuperiorPromedio, String> tcCedula;
    @FXML private TableColumn<AtletaSuperiorPromedio, String> tcNombres;
    @FXML private TableColumn<AtletaSuperiorPromedio, String> tcApellidos;
    @FXML private TableColumn<AtletaSuperiorPromedio, Integer> tcSesiones;
    @FXML private TableColumn<AtletaSuperiorPromedio, Double> tcPuntuacion;
    @FXML private TableColumn<AtletaSuperiorPromedio, Integer> tcMinutos;
    @FXML private Label lblMensaje;
    @FXML private Button btnExportarPdf;
    @FXML private Button btnVolver;

    private final ObservableList<AtletaSuperiorPromedio> datos = FXCollections.observableArrayList();
    private final AtletaSuperiorPromedioDAOJdbc dao = AtletaSuperiorPromedioDAOJdbc.getInstancia();

    @FXML
    void initialize() {
        configurarTabla();
        cargarDatos();
    }

    private void configurarTabla() {
        tcCedula.setCellValueFactory(new PropertyValueFactory<>("cedula"));
        tcNombres.setCellValueFactory(new PropertyValueFactory<>("nombres"));
        tcApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        tcSesiones.setCellValueFactory(new PropertyValueFactory<>("totalSesiones"));
        tcPuntuacion.setCellValueFactory(new PropertyValueFactory<>("puntuacionPromedio"));
        tcMinutos.setCellValueFactory(new PropertyValueFactory<>("minutosTotales"));
        tableAtletas.setItems(datos);
    }

    private void cargarDatos() {
        List<AtletaSuperiorPromedio> lista;
        try {
            lista = dao.listar();
        } catch (Exception e) {
            e.printStackTrace();
            lblMensaje.setText("Error al cargar datos: " + e.getMessage());
            return;
        }
        if (lista == null) lista = new ArrayList<>();

        datos.setAll(lista);
        if (lista.isEmpty()) {
            lblMensaje.setText("No hay atletas por encima del promedio de sesiones.");
        } else {
            lblMensaje.setText("");
        }
    }

    @FXML
    void onExportarPdf(ActionEvent event) {
        try {
            String[] headers = {"Cedula", "Nombres", "Apellidos", "Sesiones", "Punt. Prom.", "Minutos"};
            List<String[]> rows = new ArrayList<>();
            for (AtletaSuperiorPromedio a : datos) {
                rows.add(new String[]{
                        safe(a.getCedula()),
                        safe(a.getNombres()),
                        safe(a.getApellidos()),
                        safe(a.getTotalSesiones()),
                        a.getPuntuacionPromedio() != null ? String.format("%.2f", a.getPuntuacionPromedio()) : "-",
                        safe(a.getMinutosTotales())
                });
            }
            Path pdf = PdfExporter.exportar(
                    "Atletas con sesiones sobre el promedio",
                    "reporte_atletas_sobre_promedio.pdf",
                    headers,
                    rows,
                    null
            );
            lblMensaje.setText("PDF generado en: " + pdf.toAbsolutePath());
        } catch (Exception e) {
            lblMensaje.setText("Error al exportar PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String safe(Object value) {
        return value == null ? "-" : value.toString();
    }

    @FXML
    void onVolver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/co/edu/uniquindio/mindsport/mindsportpro/MenuPrincipal.fxml"));
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
