package co.edu.uniquindio.mindsport.mindsportpro.controller;

import co.edu.uniquindio.mindsport.mindsportpro.dao.ReporteResumenDAOJdbc;
import co.edu.uniquindio.mindsport.mindsportpro.model.Atleta;
import co.edu.uniquindio.mindsport.mindsportpro.model.ReporteResumen;
import co.edu.uniquindio.mindsport.mindsportpro.model.Usuario;
import co.edu.uniquindio.mindsport.mindsportpro.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

public class ReporteResumenController {

    @FXML private Label lblCedula;
    @FXML private Label lblNombre;
    @FXML private Label lblPerfil;
    @FXML private Label lblTotalSesiones;
    @FXML private Label lblPuntuacionPromedio;
    @FXML private Label lblDuracionPromedio;
    @FXML private Label lblTiempoTotal;
    @FXML private Label lblPrimeraSesion;
    @FXML private Label lblUltimaSesion;
    @FXML private Label lblDiasEntrenamiento;
    @FXML private Label lblMensaje;
    @FXML private Button btnVolverMenu;
    @FXML private BarChart<String, Number> barMetricas;
    @FXML private CategoryAxis barEjeX;
    @FXML private NumberAxis barEjeY;
    @FXML private PieChart pieDistribucion;

    private final ReporteResumenDAOJdbc reporteDAO = ReporteResumenDAOJdbc.getInstancia();

    @FXML
    void initialize() {
        System.out.println("[ReporteResumen] Inicializando vista de reporte resumen.");
        cargarReporte();
    }

    private void cargarReporte() {
        SessionManager sm = SessionManager.getInstance();
        if (!sm.isLoggedIn()) {
            mostrarMensaje("No hay usuario en sesión.");
            System.out.println("[ReporteResumen] Sin usuario en sesión, no se consulta la BD.");
            return;
        }

        Usuario usuario = sm.getUsuarioActual();
        if (!(usuario instanceof Atleta)) {
            mostrarMensaje("El reporte está disponible solo para atletas.");
            System.out.println("[ReporteResumen] Usuario no es atleta, no se consulta la BD.");
            return;
        }

        System.out.println("[ReporteResumen] Consultando resumen para cédula: " + usuario.getCedula());
        Optional<ReporteResumen> resumenOpt = reporteDAO.obtenerResumenPorAtleta(usuario.getCedula());
        if (resumenOpt.isEmpty()) {
            mostrarMensaje("No hay sesiones registradas para generar el reporte.");
            limpiarCampos();
            System.out.println("[ReporteResumen] Consulta ejecutada, sin registros de sesiones para ese atleta.");
            return;
        }

        ReporteResumen r = resumenOpt.get();
        lblMensaje.setText("");
        lblCedula.setText(valorTexto(r.getCedula()));
        lblNombre.setText((valorTexto(r.getNombres()) + " " + valorTexto(r.getApellidos())).trim());
        lblPerfil.setText(valorTexto(r.getPerfilDeportivoTexto()));
        lblTotalSesiones.setText(valorTexto(r.getTotalSesiones()));
        lblPuntuacionPromedio.setText(valorTexto(r.getPuntuacionPromedio()));
        lblDuracionPromedio.setText(valorTexto(r.getDuracionPromedioMinutos()));
        lblTiempoTotal.setText(valorTexto(r.getTiempoTotalEntrenadoMinutos()));
        lblPrimeraSesion.setText(formatearFecha(r.getPrimeraSesion()));
        lblUltimaSesion.setText(formatearFecha(r.getUltimaSesion()));
        lblDiasEntrenamiento.setText(valorTexto(r.getDiasEntrenamiento()));

        poblarBarChart(r);
        poblarPieChart(r);
        System.out.println("[ReporteResumen] Datos cargados correctamente en pantalla.");
    }

    private void limpiarCampos() {
        lblCedula.setText("-");
        lblNombre.setText("-");
        lblPerfil.setText("-");
        lblTotalSesiones.setText("-");
        lblPuntuacionPromedio.setText("-");
        lblDuracionPromedio.setText("-");
        lblTiempoTotal.setText("-");
        lblPrimeraSesion.setText("-");
        lblUltimaSesion.setText("-");
        lblDiasEntrenamiento.setText("-");
        if (barMetricas != null) {
            barMetricas.getData().clear();
        }
        if (pieDistribucion != null) {
            pieDistribucion.setData(FXCollections.observableArrayList());
        }
    }

    private void mostrarMensaje(String msg) {
        lblMensaje.setText(msg);
    }

    private String valorTexto(Object o) {
        return o == null ? "-" : o.toString();
    }

    private String formatearFecha(LocalDate fecha) {
        return fecha == null ? "-" : fecha.toString();
    }

    private void poblarBarChart(ReporteResumen r) {
        if (barMetricas == null) return;
        barMetricas.getData().clear();

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Resumen");
        serie.getData().add(new XYChart.Data<>("Sesiones", numero(r.getTotalSesiones())));
        serie.getData().add(new XYChart.Data<>("Punt. Prom", numero(r.getPuntuacionPromedio())));
        serie.getData().add(new XYChart.Data<>("Durac. Prom (min)", numero(r.getDuracionPromedioMinutos())));
        serie.getData().add(new XYChart.Data<>("Tiempo Total (min)", numero(r.getTiempoTotalEntrenadoMinutos())));
        serie.getData().add(new XYChart.Data<>("Dias Entrenados", numero(r.getDiasEntrenamiento())));

        barMetricas.getData().add(serie);
        System.out.println("[ReporteResumen] BarChart cargado con " + serie.getData().size() + " puntos.");
    }

    private void poblarPieChart(ReporteResumen r) {
        if (pieDistribucion == null) return;

        int totalSesiones = safeInt(r.getTotalSesiones());
        int dias = safeInt(r.getDiasEntrenamiento());
        int tiempoTotal = safeInt(r.getTiempoTotalEntrenadoMinutos());
        int duracionProm = (int) safeDouble(r.getDuracionPromedioMinutos());

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        data.add(new PieChart.Data("Sesiones", totalSesiones));
        data.add(new PieChart.Data("Dias entrenados", dias));
        data.add(new PieChart.Data("Tiempo total (min)", tiempoTotal));
        data.add(new PieChart.Data("Durac. prom (min)", duracionProm));

        pieDistribucion.setData(data);
        System.out.println("[ReporteResumen] PieChart cargado con " + data.size() + " porciones.");
    }

    private Number numero(Number n) {
        if (n == null) return 0;
        return n;
    }

    private int safeInt(Integer n) {
        return n == null ? 0 : n;
    }

    private double safeDouble(Double n) {
        return n == null ? 0d : n;
    }

    @FXML
    void onVolverMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uniquindio/mindsport/mindsportpro/MenuPrincipal.fxml"));
            Parent root = loader.load();

            Stage stage = SessionManager.getInstance().getStagePrincipal();
            Scene scene = new Scene(root, 600, 500);
            stage.setScene(scene);
            stage.setTitle("Menú Principal - MindSport Pro");

            System.out.println("Volviendo al menú principal desde reporte resumen");
        } catch (IOException e) {
            System.err.println("Error al volver al menú: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
