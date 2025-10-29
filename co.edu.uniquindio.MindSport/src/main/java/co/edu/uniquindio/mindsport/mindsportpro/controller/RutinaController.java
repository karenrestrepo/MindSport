package co.edu.uniquindio.mindsport.mindsportpro.controller;

import co.edu.uniquindio.mindsport.mindsportpro.dao.EjercicioDAO;
import co.edu.uniquindio.mindsport.mindsportpro.dao.RutinaDAO;
import co.edu.uniquindio.mindsport.mindsportpro.dao.UsuarioDAO;
import co.edu.uniquindio.mindsport.mindsportpro.enums.NivelDificultad;
import co.edu.uniquindio.mindsport.mindsportpro.model.Ejercicio;
import co.edu.uniquindio.mindsport.mindsportpro.model.Rutina;
import co.edu.uniquindio.mindsport.mindsportpro.model.Usuario;
import co.edu.uniquindio.mindsport.mindsportpro.model.Coach;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RutinaController {

    @FXML private Button btnActualizarRutina;
    @FXML private Button btnAgregarRutina;
    @FXML private Button btnEliminarRutina;
    @FXML private ComboBox<Coach> cbCoach;
    @FXML private ComboBox<NivelDificultad> cbDificultadRutina;
    @FXML private ListView<Ejercicio> listEjerciciosRutina;
    @FXML private TableView<Rutina> tableRutina;
    @FXML private TableColumn<Rutina, String> tcIdRutina;
    @FXML private TableColumn<Rutina, String> tcTituloRutina;
    @FXML private TableColumn<Rutina, String> tcDuracion;
    @FXML private TableColumn<Rutina, String> tcDificultadRutina;
    @FXML private TableColumn<Rutina, String> tcCoachRutina;
    @FXML private TextField txtTituloRutina;
    @FXML private TextField txtDescripcionRutina;
    @FXML private TextField txtDuracionRutina;
    @FXML private TextField txtFiltrarRutina;
    @FXML private VBox vboxAtleta;
    @FXML private VBox vboxCoach;

    private final RutinaDAO rutinaDAO = RutinaDAO.getInstancia();
    private final EjercicioDAO ejercicioDAO = EjercicioDAO.getInstancia();
    private final UsuarioDAO usuarioDAO = UsuarioDAO.getInstancia();

    private final ObservableList<Rutina> listaRutinas = FXCollections.observableArrayList();
    private final ObservableList<Ejercicio> listaEjercicios = FXCollections.observableArrayList();
    private final ObservableList<Coach> listaCoaches = FXCollections.observableArrayList();

    private MindSportController controladorPrincipal;

    @FXML
    void onCoach(ActionEvent event) { }

    @FXML
    void onDificultad(ActionEvent event) { }

    @FXML
    void initialize() {
        System.out.println("🔧 Inicializando RutinaController...");

        cbDificultadRutina.setItems(FXCollections.observableArrayList(NivelDificultad.values()));

        // Configurar ListView de ejercicios
        listEjerciciosRutina.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listEjerciciosRutina.setCellFactory(param -> new ListCell<Ejercicio>() {
            @Override
            protected void updateItem(Ejercicio ejercicio, boolean empty) {
                super.updateItem(ejercicio, empty);
                if (empty || ejercicio == null) {
                    setText(null);
                } else {
                    setText(ejercicio.getTitulo() + " (" + ejercicio.getDuracion() + " min)");
                }
            }
        });

        // Configurar ComboBox de coaches
        cbCoach.setCellFactory(param -> new ListCell<Coach>() {
            @Override
            protected void updateItem(Coach coach, boolean empty) {
                super.updateItem(coach, empty);
                if (empty || coach == null) {
                    setText(null);
                } else {
                    setText(coach.getNombres() + " " + coach.getApellidos() + " - CC: " + coach.getCedula());
                }
            }
        });

        cbCoach.setButtonCell(new ListCell<Coach>() {
            @Override
            protected void updateItem(Coach coach, boolean empty) {
                super.updateItem(coach, empty);
                if (empty || coach == null) {
                    setText(null);
                } else {
                    setText(coach.getNombres() + " " + coach.getApellidos());
                }
            }
        });

        configurarColumnas();
        configurarFiltrado();

        // Listener para selección en tabla
        tableRutina.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                cargarRutinaEnFormulario(newSel);
            }
        });

        System.out.println("✅ RutinaController inicializado");
    }

    private void configurarColumnas() {
        tcIdRutina.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getId() != null ? String.valueOf(cellData.getValue().getId()) : "")
        );

        tcTituloRutina.setCellValueFactory(cellData ->
                new SimpleStringProperty(safeString(cellData.getValue().getTitulo()))
        );

        tcDuracion.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDuracionEstimada() != null ?
                        String.valueOf(cellData.getValue().getDuracionEstimada()) : "")
        );

        tcDificultadRutina.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNivelDificultad() != null ?
                        cellData.getValue().getNivelDificultad().name() : "")
        );

        tcCoachRutina.setCellValueFactory(cellData -> {
            String cedula = cellData.getValue().getCedulaCoach();
            if (cedula == null || cedula.trim().isEmpty()) {
                return new SimpleStringProperty("");
            }

            Optional<Usuario> coachOpt = usuarioDAO.buscarPorCedula(cedula);
            if (coachOpt.isPresent()) {
                Usuario coach = coachOpt.get();
                return new SimpleStringProperty(coach.getNombres() + " " + coach.getApellidos());
            }
            return new SimpleStringProperty("Coach no encontrado");
        });
    }

    private void configurarFiltrado() {
        FilteredList<Rutina> filteredData = new FilteredList<>(listaRutinas, p -> true);
        txtFiltrarRutina.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(rutina -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String filtro = newVal.toLowerCase();

                if (safeString(rutina.getTitulo()).toLowerCase().contains(filtro)) return true;
                if (safeString(rutina.getDescripcion()).toLowerCase().contains(filtro)) return true;
                if (rutina.getId() != null && String.valueOf(rutina.getId()).contains(filtro)) return true;
                if (safeString(rutina.getCedulaCoach()).contains(filtro)) return true;

                return false;
            });
        });

        SortedList<Rutina> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableRutina.comparatorProperty());
        tableRutina.setItems(sortedData);
    }

    // Método llamado por el controlador principal
    public void setControladorPrincipal(MindSportController controlador) {
        this.controladorPrincipal = controlador;
        System.out.println("🔗 RutinaController conectado al controlador principal");
    }

    // Método llamado cuando se cambia a esta pestaña
    public void refrescarDatos() {
        System.out.println("🔄 Refrescando datos en RutinaController...");
        cargarEjercicios();
        cargarCoaches();
        cargarRutinas();
    }

    // Método llamado cuando hay cambios externos (en usuarios o ejercicios)
    public void actualizarDatosExternos() {
        System.out.println("🔄 Actualizando datos externos en RutinaController...");
        cargarEjercicios();
        cargarCoaches();
    }

    private void cargarEjercicios() {
        List<Ejercicio> ejercicios = ejercicioDAO.listar();
        listaEjercicios.setAll(ejercicios);
        System.out.println("   ✓ Ejercicios cargados: " + ejercicios.size());
    }

    private void cargarCoaches() {
        List<Coach> coaches = usuarioDAO.listar().stream()
                .filter(u -> u instanceof Coach)
                .map(u -> (Coach) u)
                .collect(Collectors.toList());
        listaCoaches.setAll(coaches);
        System.out.println("   ✓ Coaches cargados: " + coaches.size());
    }

    private void cargarRutinas() {
        List<Rutina> rutinas = rutinaDAO.listar();
        listaRutinas.setAll(rutinas);
        System.out.println("   ✓ Rutinas cargadas: " + rutinas.size());
    }

    @FXML
    void onAgregarRutina(ActionEvent event) {
        String titulo = txtTituloRutina.getText().trim();
        if (titulo.isEmpty()) {
            mostrarAlerta("El título es obligatorio.");
            return;
        }

        Rutina r = new Rutina();
        r.setTitulo(titulo);
        r.setDescripcion(txtDescripcionRutina.getText().trim());
        r.setDuracionEstimada(parseInteger(txtDuracionRutina.getText().trim()));
        r.setNivelDificultad(cbDificultadRutina.getValue());

        Coach coachSel = cbCoach.getValue();
        if (coachSel != null) {
            r.setCedulaCoach(coachSel.getCedula());
        }

        List<Ejercicio> seleccionados = new ArrayList<>(listEjerciciosRutina.getSelectionModel().getSelectedItems());
        r.setEjercicios(seleccionados);

        rutinaDAO.crear(r);
        cargarRutinas();
        limpiarCampos();
        mostrarAlerta("Rutina agregada exitosamente.");

        // Notificar al controlador principal
        if (controladorPrincipal != null) {
            controladorPrincipal.notificarCambioRutina();
        }
    }

    @FXML
    void onActualizarRutina(ActionEvent event) {
        Rutina sel = tableRutina.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta("Seleccione una rutina para actualizar.");
            return;
        }

        sel.setTitulo(txtTituloRutina.getText().trim());
        sel.setDescripcion(txtDescripcionRutina.getText().trim());
        sel.setDuracionEstimada(parseInteger(txtDuracionRutina.getText().trim()));
        sel.setNivelDificultad(cbDificultadRutina.getValue());

        Coach coachSel = cbCoach.getValue();
        if (coachSel != null) {
            sel.setCedulaCoach(coachSel.getCedula());
        }

        List<Ejercicio> seleccionados = new ArrayList<>(listEjerciciosRutina.getSelectionModel().getSelectedItems());
        sel.setEjercicios(seleccionados);

        boolean ok = rutinaDAO.actualizar(sel);
        if (ok) {
            mostrarAlerta("Rutina actualizada exitosamente.");
        } else {
            mostrarAlerta("No fue posible actualizar la rutina.");
        }
        cargarRutinas();

        if (controladorPrincipal != null) {
            controladorPrincipal.notificarCambioRutina();
        }
    }

    @FXML
    void onEliminararRutina(ActionEvent event) {
        Rutina sel = tableRutina.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta("Seleccione una rutina para eliminar.");
            return;
        }

        rutinaDAO.eliminar(sel);
        cargarRutinas();
        limpiarCampos();
        mostrarAlerta("Rutina eliminada exitosamente.");

        if (controladorPrincipal != null) {
            controladorPrincipal.notificarCambioRutina();
        }
    }

    private void cargarRutinaEnFormulario(Rutina r) {
        if (r == null) return;

        txtTituloRutina.setText(safeString(r.getTitulo()));
        txtDescripcionRutina.setText(safeString(r.getDescripcion()));
        txtDuracionRutina.setText(r.getDuracionEstimada() != null ? String.valueOf(r.getDuracionEstimada()) : "");
        cbDificultadRutina.setValue(r.getNivelDificultad());

        if (r.getCedulaCoach() != null && !r.getCedulaCoach().trim().isEmpty()) {
            Coach coach = listaCoaches.stream()
                    .filter(c -> c.getCedula() != null && c.getCedula().equals(r.getCedulaCoach()))
                    .findFirst()
                    .orElse(null);
            cbCoach.setValue(coach);
        } else {
            cbCoach.setValue(null);
        }

        listEjerciciosRutina.getSelectionModel().clearSelection();
        if (r.getEjercicios() != null) {
            for (Ejercicio ej : r.getEjercicios()) {
                for (int i = 0; i < listaEjercicios.size(); i++) {
                    if (listaEjercicios.get(i).getId() != null &&
                            listaEjercicios.get(i).getId().equals(ej.getId())) {
                        listEjerciciosRutina.getSelectionModel().select(i);
                        break;
                    }
                }
            }
        }
    }

    private void limpiarCampos() {
        txtTituloRutina.clear();
        txtDescripcionRutina.clear();
        txtDuracionRutina.clear();
        cbDificultadRutina.setValue(null);
        cbCoach.setValue(null);
        listEjerciciosRutina.getSelectionModel().clearSelection();
        tableRutina.getSelectionModel().clearSelection();
    }

    private Integer parseInteger(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void mostrarAlerta(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private String safeString(String s) {
        return s == null ? "" : s;
    }
}