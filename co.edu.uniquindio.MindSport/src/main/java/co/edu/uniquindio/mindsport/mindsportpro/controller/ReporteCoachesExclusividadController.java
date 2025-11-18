package co.edu.uniquindio.mindsport.mindsportpro.controller;

import co.edu.uniquindio.mindsport.mindsportpro.dao.CoachExclusividadDAOJdbc;
import co.edu.uniquindio.mindsport.mindsportpro.model.CoachExclusividad;
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

public class ReporteCoachesExclusividadController {

    @FXML private TableView<CoachExclusividad> tableCoaches;
    @FXML private TableColumn<CoachExclusividad, String> tcCedula;
    @FXML private TableColumn<CoachExclusividad, String> tcNombres;
    @FXML private TableColumn<CoachExclusividad, String> tcApellidos;
    @FXML private TableColumn<CoachExclusividad, Integer> tcAtendidos;
    @FXML private TableColumn<CoachExclusividad, Integer> tcExclusivos;
    @FXML private Label lblMensaje;
    @FXML private Button btnExportarPdf;
    @FXML private Button btnVolver;

    private final ObservableList<CoachExclusividad> datos = FXCollections.observableArrayList();
    private final CoachExclusividadDAOJdbc dao = CoachExclusividadDAOJdbc.getInstancia();

    @FXML
    void initialize() {
        configurarTabla();
        cargarDatos();
    }

    private void configurarTabla() {
        tcCedula.setCellValueFactory(new PropertyValueFactory<>("cedulaCoach"));
        tcNombres.setCellValueFactory(new PropertyValueFactory<>("nombresCoach"));
        tcApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidosCoach"));
        tcAtendidos.setCellValueFactory(new PropertyValueFactory<>("atletasAtendidos"));
        tcExclusivos.setCellValueFactory(new PropertyValueFactory<>("atletasExclusivos"));
        tableCoaches.setItems(datos);
    }

    private void cargarDatos() {
        List<CoachExclusividad> lista;
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
            lblMensaje.setText("No hay sesiones registradas para calcular este reporte.");
        } else {
            lblMensaje.setText("");
        }
    }

    @FXML
    void onExportarPdf(ActionEvent event) {
        try {
            String[] headers = {"Cedula", "Nombres", "Apellidos", "Atendidos", "Exclusivos"};
            List<String[]> rows = new ArrayList<>();
            for (CoachExclusividad c : datos) {
                rows.add(new String[]{
                        safe(c.getCedulaCoach()),
                        safe(c.getNombresCoach()),
                        safe(c.getApellidosCoach()),
                        safe(c.getAtletasAtendidos()),
                        safe(c.getAtletasExclusivos())
                });
            }
            Path pdf = PdfExporter.exportar(
                    "Coaches con atletas exclusivos",
                    "reporte_coaches_exclusividad.pdf",
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
