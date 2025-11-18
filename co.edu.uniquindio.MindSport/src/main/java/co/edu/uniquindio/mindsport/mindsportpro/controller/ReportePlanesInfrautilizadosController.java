package co.edu.uniquindio.mindsport.mindsportpro.controller;

import co.edu.uniquindio.mindsport.mindsportpro.dao.PlanInfrautilizadoDAOJdbc;
import co.edu.uniquindio.mindsport.mindsportpro.model.PlanInfrautilizado;
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

public class ReportePlanesInfrautilizadosController {

    @FXML private TableView<PlanInfrautilizado> tablePlanes;
    @FXML private TableColumn<PlanInfrautilizado, Integer> tcIdPlan;
    @FXML private TableColumn<PlanInfrautilizado, String> tcNombrePlan;
    @FXML private TableColumn<PlanInfrautilizado, Integer> tcInscripciones;
    @FXML private TableColumn<PlanInfrautilizado, Integer> tcConSesiones;
    @FXML private TableColumn<PlanInfrautilizado, Integer> tcSinSesiones;
    @FXML private TableColumn<PlanInfrautilizado, Double> tcPorcentajeSin;
    @FXML private Label lblMensaje;
    @FXML private Button btnExportarPdf;
    @FXML private Button btnVolver;

    private final ObservableList<PlanInfrautilizado> datos = FXCollections.observableArrayList();
    private final PlanInfrautilizadoDAOJdbc dao = PlanInfrautilizadoDAOJdbc.getInstancia();

    @FXML
    void initialize() {
        configurarTabla();
        cargarDatos();
    }

    private void configurarTabla() {
        tcIdPlan.setCellValueFactory(new PropertyValueFactory<>("idPlan"));
        tcNombrePlan.setCellValueFactory(new PropertyValueFactory<>("nombrePlan"));
        tcInscripciones.setCellValueFactory(new PropertyValueFactory<>("totalInscripciones"));
        tcConSesiones.setCellValueFactory(new PropertyValueFactory<>("atletasConSesiones"));
        tcSinSesiones.setCellValueFactory(new PropertyValueFactory<>("atletasSinSesiones"));
        tcPorcentajeSin.setCellValueFactory(new PropertyValueFactory<>("porcentajeSinSesiones"));
        tablePlanes.setItems(datos);
    }

    private void cargarDatos() {
        List<PlanInfrautilizado> lista;
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
            lblMensaje.setText("No hay inscripciones para calcular este reporte.");
        } else {
            lblMensaje.setText("");
        }
    }

    @FXML
    void onExportarPdf(ActionEvent event) {
        try {
            String[] headers = {"ID", "Plan", "Inscripciones", "Con Sesiones", "Sin Sesiones", "% Sin Sesiones"};
            List<String[]> rows = new ArrayList<>();
            for (PlanInfrautilizado p : datos) {
                rows.add(new String[]{
                        safe(p.getIdPlan()),
                        safe(p.getNombrePlan()),
                        safe(p.getTotalInscripciones()),
                        safe(p.getAtletasConSesiones()),
                        safe(p.getAtletasSinSesiones()),
                        p.getPorcentajeSinSesiones() != null ? String.format("%.2f", p.getPorcentajeSinSesiones()) : "-"
                });
            }
            Path pdf = PdfExporter.exportar(
                    "Planes infrautilizados",
                    "reporte_planes_infrautilizados.pdf",
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
