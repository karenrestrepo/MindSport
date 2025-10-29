package co.edu.uniquindio.mindsport.mindsportpro.controller;

import co.edu.uniquindio.mindsport.mindsportpro.dao.EjercicioDAO;
import co.edu.uniquindio.mindsport.mindsportpro.enums.FaseUso;
import co.edu.uniquindio.mindsport.mindsportpro.enums.TipoEjercicio;
import co.edu.uniquindio.mindsport.mindsportpro.model.Ejercicio;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class EjercicioController {

    @FXML
    private Button btnActualizarEjercicio, btnAgregarEjercicio, btnEliminarEjercicio;

    @FXML
    private ComboBox<FaseUso> cbFaseUsoEjercicio;

    @FXML
    private ComboBox<TipoEjercicio> cbTipoEjercicio;

    @FXML
    private TableView<Ejercicio> tableEjercicio;

    @FXML
    private TableColumn<Ejercicio, String> tcIdEjercicio, tcTituloEjercicio, tcEjercicio, tcFaseUsoEjercicio, tcTipoEjercicio;

    @FXML
    private TextField txtTituloEjercicio, txtDescripcionEjercicio, txtDuracionEjercicio, txtFiltrarEjercicio;

    private final EjercicioDAO ejercicioDAO = EjercicioDAO.getInstancia();
    private final ObservableList<Ejercicio> listaEjercicios = FXCollections.observableArrayList();
    @FXML
    void onFaseUsoEjercicio(ActionEvent event) {

    }

    @FXML
    void onTipoEjercicio(ActionEvent event) {

    }

    @FXML
    void initialize() {
        // Inicializar combos con enums
        cbFaseUsoEjercicio.setItems(FXCollections.observableArrayList(FaseUso.values()));
        cbTipoEjercicio.setItems(FXCollections.observableArrayList(TipoEjercicio.values()));

        // Configurar columnas de la tabla
        tcIdEjercicio.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getId() != null ? String.valueOf(cellData.getValue().getId()) : ""
        ));
        tcTituloEjercicio.setCellValueFactory(cellData -> new SimpleStringProperty(
                safeString(cellData.getValue().getTitulo())
        ));
        tcEjercicio.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getDuracion() != null ? String.valueOf(cellData.getValue().getDuracion()) : ""
        ));
        tcFaseUsoEjercicio.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFaseUso() != null ? cellData.getValue().getFaseUso().name() : ""
        ));
        tcTipoEjercicio.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getTipoEjercicio() != null ? cellData.getValue().getTipoEjercicio().name() : ""
        ));

        // Cargar lista desde DAO
        refreshTabla();

        // Cargar datos en formulario al seleccionar
        tableEjercicio.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) cargarEjercicioEnFormulario(newSel);
        });

        // Filtro dinámico
        FilteredList<Ejercicio> filteredData = new FilteredList<>(listaEjercicios, p -> true);
        txtFiltrarEjercicio.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(ej -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String filtro = newVal.toLowerCase();
                return (safeString(ej.getTitulo()).toLowerCase().contains(filtro)) ||
                        (safeString(ej.getDescripcion()).toLowerCase().contains(filtro)) ||
                        (ej.getId() != null && String.valueOf(ej.getId()).contains(filtro));
            });
        });

        SortedList<Ejercicio> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableEjercicio.comparatorProperty());
        tableEjercicio.setItems(sortedData);
    }

    // ================= CRUD =================

    @FXML
    void onAgregarEjercicio(ActionEvent event) {
        if (txtTituloEjercicio.getText().isBlank()) {
            mostrarAlerta("El título del ejercicio es obligatorio.");
            return;
        }

        Ejercicio nuevo = new Ejercicio();
        nuevo.setTitulo(txtTituloEjercicio.getText().trim());
        nuevo.setDescripcion(txtDescripcionEjercicio.getText().trim());
        nuevo.setDuracion(parseInteger(txtDuracionEjercicio.getText().trim()));
        nuevo.setFaseUso(cbFaseUsoEjercicio.getValue());
        nuevo.setTipoEjercicio(cbTipoEjercicio.getValue());

        ejercicioDAO.crear(nuevo);
        refreshTabla();
        limpiarCampos();

        if (controladorPrincipal != null) {
            controladorPrincipal.notificarCambioEjercicio();
        }
    }

    @FXML
    void onActualizarEjercicio(ActionEvent event) {
        Ejercicio seleccionado = tableEjercicio.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Seleccione un ejercicio para actualizar.");
            return;
        }

        seleccionado.setTitulo(txtTituloEjercicio.getText().trim());
        seleccionado.setDescripcion(txtDescripcionEjercicio.getText().trim());
        seleccionado.setDuracion(parseInteger(txtDuracionEjercicio.getText().trim()));
        seleccionado.setFaseUso(cbFaseUsoEjercicio.getValue());
        seleccionado.setTipoEjercicio(cbTipoEjercicio.getValue());

        boolean ok = ejercicioDAO.actualizar(seleccionado);
        if (!ok) mostrarAlerta("No fue posible actualizar el ejercicio.");
        refreshTabla();

        if (controladorPrincipal != null) {
            controladorPrincipal.notificarCambioEjercicio();
        }
    }

    @FXML
    void onEliminararEjercicio(ActionEvent event) {
        Ejercicio seleccionado = tableEjercicio.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Seleccione un ejercicio para eliminar.");
            return;
        }

        ejercicioDAO.eliminar(seleccionado);
        refreshTabla();
        limpiarCampos();

        if (controladorPrincipal != null) {
            controladorPrincipal.notificarCambioEjercicio();
        }
    }

    // ================= Helpers =================

    private void refreshTabla() {
        listaEjercicios.setAll(ejercicioDAO.listar());
    }

    private void cargarEjercicioEnFormulario(Ejercicio e) {
        txtTituloEjercicio.setText(safeString(e.getTitulo()));
        txtDescripcionEjercicio.setText(safeString(e.getDescripcion()));
        txtDuracionEjercicio.setText(e.getDuracion() != null ? String.valueOf(e.getDuracion()) : "");
        cbFaseUsoEjercicio.setValue(e.getFaseUso());
        cbTipoEjercicio.setValue(e.getTipoEjercicio());
    }

    private void limpiarCampos() {
        txtTituloEjercicio.clear();
        txtDescripcionEjercicio.clear();
        txtDuracionEjercicio.clear();
        cbFaseUsoEjercicio.setValue(null);
        cbTipoEjercicio.setValue(null);
        tableEjercicio.getSelectionModel().clearSelection();
    }

    private Integer parseInteger(String s) {
        if (s == null || s.isEmpty()) return null;
        try { return Integer.parseInt(s.trim()); }
        catch (NumberFormatException ex) { return null; }
    }

    private void mostrarAlerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private String safeString(String s) {
        return s == null ? "" : s;
    }

    private MindSportController controladorPrincipal;

    // Método público para conectar
    public void setControladorPrincipal(MindSportController controlador) {
        this.controladorPrincipal = controlador;
        System.out.println("🔗 EjercicioController conectado al controlador principal");
    }

    // Método para refrescar datos
    public void refrescarDatos() {
        System.out.println("🔄 Refrescando datos en EjercicioController...");
        refreshTabla();
    }
}



