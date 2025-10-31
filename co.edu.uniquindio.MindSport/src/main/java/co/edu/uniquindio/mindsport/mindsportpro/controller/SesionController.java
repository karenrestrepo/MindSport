package co.edu.uniquindio.mindsport.mindsportpro.controller;

import co.edu.uniquindio.mindsport.mindsportpro.dao.*;
import co.edu.uniquindio.mindsport.mindsportpro.model.Rutina;
import co.edu.uniquindio.mindsport.mindsportpro.model.Sesion;
import co.edu.uniquindio.mindsport.mindsportpro.model.Usuario;
import co.edu.uniquindio.mindsport.mindsportpro.model.Atleta;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SesionController {

    @FXML private Button btnActualizarSesion;
    @FXML private Button btnAgregarSesion;
    @FXML private Button btnEliminarSesion;

    @FXML private ComboBox<Atleta> cbAtleta;
    @FXML private ComboBox<Rutina> cbRutina;
    @FXML private DatePicker dpFechaSesion;

    @FXML private TableView<Sesion> tableSesion;
    @FXML private TableColumn<Sesion, String> tcIdSesion, tcAtleta, tcRutina, tcFecha, tcDuracion, tcScore, tcObservaciones;

    @FXML private TextField txtDuracionSesion, txtFiltrarSesion, txtPuntuacionSesion;
    @FXML private TextArea txtaObservaciones;

    // DAOs
    private final SesionDAOJdbc sesionDAO = SesionDAOJdbc.getInstancia();
    private final UsuarioDAOJdbc usuarioDAO = UsuarioDAOJdbc.getInstancia();
    private final RutinaDAOJdbc rutinaDAO = RutinaDAOJdbc.getInstancia();

    // listas observables
    private final ObservableList<Sesion> listaSesiones = FXCollections.observableArrayList();
    private final ObservableList<Atleta> listaAtletas = FXCollections.observableArrayList();
    private final ObservableList<Rutina> listaRutinas = FXCollections.observableArrayList();

    // referencia al controlador principal (opcional, para integración)
    private MindSportController controladorPrincipal;

    @FXML
    void initialize() {
        // ============================================
        // CONFIGURAR COMBOBOX DE ATLETA
        // ============================================
        cbAtleta.setCellFactory(param -> new ListCell<Atleta>() {
            @Override
            protected void updateItem(Atleta atleta, boolean empty) {
                super.updateItem(atleta, empty);
                if (empty || atleta == null) {
                    setText(null);
                } else {
                    setText(atleta.getNombres() + " " + atleta.getApellidos() + " - CC: " + atleta.getCedula());
                }
            }
        });

        cbAtleta.setButtonCell(new ListCell<Atleta>() {
            @Override
            protected void updateItem(Atleta atleta, boolean empty) {
                super.updateItem(atleta, empty);
                if (empty || atleta == null) {
                    setText(null);
                } else {
                    setText(atleta.getNombres() + " " + atleta.getApellidos());
                }
            }
        });

        // ============================================
        // CONFIGURAR COMBOBOX DE RUTINA
        // ============================================
        cbRutina.setCellFactory(param -> new ListCell<Rutina>() {
            @Override
            protected void updateItem(Rutina rutina, boolean empty) {
                super.updateItem(rutina, empty);
                if (empty || rutina == null) {
                    setText(null);
                } else {
                    String duracion = rutina.getDuracionEstimada() != null ?
                            " (" + rutina.getDuracionEstimada() + " min)" : "";
                    setText(rutina.getTitulo() + duracion);
                }
            }
        });

        cbRutina.setButtonCell(new ListCell<Rutina>() {
            @Override
            protected void updateItem(Rutina rutina, boolean empty) {
                super.updateItem(rutina, empty);
                if (empty || rutina == null) {
                    setText(null);
                } else {
                    setText(rutina.getTitulo());
                }
            }
        });

        // ============================================
        // CARGAR DATOS INICIALES
        // ============================================
        List<Atleta> atletas = usuarioDAO.listar().stream()
                .filter(u -> u instanceof Atleta)
                .map(u -> (Atleta) u)
                .collect(Collectors.toList());
        listaAtletas.setAll(atletas);
        cbAtleta.setItems(listaAtletas);

        listaRutinas.setAll(rutinaDAO.listar());
        cbRutina.setItems(listaRutinas);

        // ============================================
        // CONFIGURAR COLUMNAS DE LA TABLA
        // ============================================
        tcIdSesion.setCellValueFactory(s -> new SimpleStringProperty(
                s.getValue().getId() == null ? "" : String.valueOf(s.getValue().getId())
        ));

        tcAtleta.setCellValueFactory(s -> {
            String ced = s.getValue().getCedulaAtleta();
            String nombre = "";
            if (ced != null) {
                Usuario u = usuarioDAO.listar().stream()
                        .filter(x -> ced.equals(x.getCedula()))
                        .findFirst()
                        .orElse(null);
                if (u != null) {
                    nombre = u.getNombres() + " " + u.getApellidos();
                }
            }
            return new SimpleStringProperty(nombre);
        });

        tcRutina.setCellValueFactory(s -> {
            Integer idRut = s.getValue().getRutinaId();
            String t = "";
            if (idRut != null) {
                Rutina r = rutinaDAO.listar().stream()
                        .filter(x -> idRut.equals(x.getId()))
                        .findFirst()
                        .orElse(null);
                if (r != null) t = r.getTitulo();
            }
            return new SimpleStringProperty(t);
        });

        tcFecha.setCellValueFactory(s -> new SimpleStringProperty(
                s.getValue().getFecha() == null ? "" : s.getValue().getFecha().toString()
        ));

        tcDuracion.setCellValueFactory(s -> new SimpleStringProperty(
                s.getValue().getDuracionReal() == null ? "" : String.valueOf(s.getValue().getDuracionReal())
        ));

        tcScore.setCellValueFactory(s -> new SimpleStringProperty(
                s.getValue().getPuntuacion() == null ? "" : String.valueOf(s.getValue().getPuntuacion())
        ));

        tcObservaciones.setCellValueFactory(s -> new SimpleStringProperty(
                s.getValue().getObservacionCoach() == null ? "" : s.getValue().getObservacionCoach()
        ));

        // ============================================
        // CARGAR DATOS EN LA TABLA
        // ============================================
        refreshTabla();

        // ============================================
        // LISTENER PARA SELECCIÓN EN TABLA
        // ============================================
        tableSesion.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) cargarSesionEnFormulario(newSel);
        });

        // ============================================
        // FILTRO DINÁMICO
        // ============================================
        FilteredList<Sesion> filteredData = new FilteredList<>(listaSesiones, p -> true);
        txtFiltrarSesion.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(s -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String f = newVal.toLowerCase();

                // Buscar por cédula atleta
                if (s.getCedulaAtleta() != null && s.getCedulaAtleta().toLowerCase().contains(f))
                    return true;

                // Buscar por fecha
                if (s.getFecha() != null && s.getFecha().toString().contains(f))
                    return true;

                // Buscar por observaciones
                if (s.getObservacionCoach() != null && s.getObservacionCoach().toLowerCase().contains(f))
                    return true;

                // Buscar por título de rutina
                if (s.getRutinaId() != null) {
                    Rutina r = rutinaDAO.listar().stream()
                            .filter(x -> s.getRutinaId().equals(x.getId()))
                            .findFirst()
                            .orElse(null);
                    if (r != null && r.getTitulo() != null && r.getTitulo().toLowerCase().contains(f))
                        return true;
                }

                // Buscar por nombre de atleta
                if (s.getCedulaAtleta() != null) {
                    Usuario u = usuarioDAO.listar().stream()
                            .filter(x -> s.getCedulaAtleta().equals(x.getCedula()))
                            .findFirst()
                            .orElse(null);
                    if (u != null) {
                        String nombreCompleto = (u.getNombres() + " " + u.getApellidos()).toLowerCase();
                        if (nombreCompleto.contains(f)) return true;
                    }
                }

                return false;
            });
        });

        SortedList<Sesion> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableSesion.comparatorProperty());
        tableSesion.setItems(sortedData);
    }

    // ================= CRUD =================

    @FXML
    void onAgregarSesion(ActionEvent event) {
        if (cbAtleta.getValue() == null) { mostrarAlerta("Seleccione un atleta."); return; }
        if (cbRutina.getValue() == null) { mostrarAlerta("Seleccione una rutina."); return; }

        Sesion s = new Sesion();
        s.setCedulaAtleta(cbAtleta.getValue().getCedula());
        s.setRutinaId(cbRutina.getValue().getId());
        s.setFecha(dpFechaSesion.getValue() == null ? LocalDate.now() : dpFechaSesion.getValue());
        s.setDuracionReal(parseInteger(txtDuracionSesion.getText().trim()));
        s.setPuntuacion(parseDouble(txtPuntuacionSesion.getText().trim()));
        s.setObservacionCoach(txtaObservaciones.getText().trim());

        sesionDAO.crear(s);
        refreshTabla();
        limpiarCampos();

        // notificar al principal que hay cambios (si aplica)
        if (controladorPrincipal != null) controladorPrincipal.notificarCambioRutina();
    }

    @FXML
    void onActualizarSesion(ActionEvent event) {
        Sesion sel = tableSesion.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarAlerta("Seleccione una sesión para actualizar."); return; }

        if (cbAtleta.getValue() != null) sel.setCedulaAtleta(cbAtleta.getValue().getCedula());
        if (cbRutina.getValue() != null) sel.setRutinaId(cbRutina.getValue().getId());
        sel.setFecha(dpFechaSesion.getValue());
        sel.setDuracionReal(parseInteger(txtDuracionSesion.getText().trim()));
        sel.setPuntuacion(parseDouble(txtPuntuacionSesion.getText().trim()));
        sel.setObservacionCoach(txtaObservaciones.getText().trim());

        boolean ok = sesionDAO.actualizar(sel);
        if (!ok) mostrarAlerta("No fue posible actualizar la sesión.");
        refreshTabla();
    }

    @FXML
    void onEliminararSesion(ActionEvent event) {
        Sesion sel = tableSesion.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarAlerta("Seleccione una sesión para eliminar."); return; }
        sesionDAO.eliminar(sel);
        refreshTabla();
        limpiarCampos();
    }

    // handlers vacíos para FXML (evitan LoadException si onAction está)
    @FXML void onAtleta(ActionEvent event) {

    }
    @FXML void onRutina(ActionEvent event)  { }

    // =============== Helpers / Integración con MindSportController ===============

    private void refreshTabla() {
        listaSesiones.setAll(sesionDAO.listar());
    }

    private void cargarSesionEnFormulario(Sesion s) {
        if (s == null) return;
        // seleccionar atleta por cédula
        if (s.getCedulaAtleta() != null) {
            Atleta a = listaAtletas.stream().filter(x -> s.getCedulaAtleta().equals(x.getCedula())).findFirst().orElse(null);
            cbAtleta.setValue(a);
        } else cbAtleta.setValue(null);

        // seleccionar rutina por id
        if (s.getRutinaId() != null) {
            Rutina r = listaRutinas.stream().filter(x -> s.getRutinaId().equals(x.getId())).findFirst().orElse(null);
            cbRutina.setValue(r);
        } else cbRutina.setValue(null);

        dpFechaSesion.setValue(s.getFecha());
        txtDuracionSesion.setText(s.getDuracionReal() == null ? "" : String.valueOf(s.getDuracionReal()));
        txtPuntuacionSesion.setText(s.getPuntuacion() == null ? "" : String.valueOf(s.getPuntuacion()));
        txtaObservaciones.setText(s.getObservacionCoach() == null ? "" : s.getObservacionCoach());
    }

    private void limpiarCampos() {
        cbAtleta.setValue(null);
        cbRutina.setValue(null);
        dpFechaSesion.setValue(null);
        txtDuracionSesion.clear();
        txtPuntuacionSesion.clear();
        txtaObservaciones.clear();
        tableSesion.getSelectionModel().clearSelection();
    }

    private Integer parseInteger(String s) {
        if (s == null || s.isEmpty()) return null;
        try { return Integer.parseInt(s.trim()); } catch (NumberFormatException ex) { return null; }
    }

    private Double parseDouble(String s) {
        if (s == null || s.isEmpty()) return null;
        try { return Double.parseDouble(s.trim().replace(",", ".")); } catch (NumberFormatException ex) { return null; }
    }

    private void mostrarAlerta(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    // Métodos para que MindSportController se integre
    public void setControladorPrincipal(MindSportController ctrl) {
        this.controladorPrincipal = ctrl;
    }

    // llamado por el controlador principal cuando hay cambios externos (usuarios/rutinas/ejercicios)
    public void actualizarDatosExternos() {
        System.out.println("🔄 Actualizando datos externos en SesionController...");

        // Cargar atletas
        List<Atleta> atletas = usuarioDAO.listar().stream()
                .filter(u -> u instanceof Atleta)
                .map(u -> (Atleta) u)
                .collect(Collectors.toList());
        Platform.runLater(() -> {
            listaAtletas.setAll(atletas);
            cbAtleta.setItems(listaAtletas);
            System.out.println("   ✓ Atletas cargados: " + atletas.size());
        });

        // Cargar rutinas
        List<Rutina> rutinas = rutinaDAO.listar();
        Platform.runLater(() -> {
            listaRutinas.setAll(rutinas);
            cbRutina.setItems(listaRutinas);
            System.out.println("   ✓ Rutinas cargadas: " + rutinas.size());
        });

        // Cargar sesiones
        List<Sesion> sesiones = sesionDAO.listar();
        Platform.runLater(() -> {
            listaSesiones.setAll(sesiones);
            System.out.println("   ✓ Sesiones cargadas: " + sesiones.size());
        });
    }

    public void refrescarDatos() {
        System.out.println("🔄 Refrescando datos en SesionController...");
        actualizarDatosExternos();
    }

}

