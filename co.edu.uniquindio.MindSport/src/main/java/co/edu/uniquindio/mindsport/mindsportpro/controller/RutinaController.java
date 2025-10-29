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
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
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

    // DAOs
    private final RutinaDAO rutinaDAO = RutinaDAO.getInstancia();
    private final EjercicioDAO ejercicioDAO = EjercicioDAO.getInstancia();
    private final UsuarioDAO usuarioDAO = UsuarioDAO.getInstancia();

    // Listas observables
    private final ObservableList<Rutina> listaRutinas = FXCollections.observableArrayList();
    private final ObservableList<Ejercicio> listaEjercicios = FXCollections.observableArrayList();
    private final ObservableList<Coach> listaCoaches = FXCollections.observableArrayList();
    @FXML
    void onCoach(ActionEvent event) {

    }

    @FXML
    void onDificultad(ActionEvent event) {

    }

    @FXML
    void initialize() {
        // Cargar los valores del enum directamente
        cbDificultadRutina.setItems(FXCollections.observableArrayList(NivelDificultad.values()));

        // Cargar ejercicios
        listaEjercicios.setAll(ejercicioDAO.listar());
        listEjerciciosRutina.setItems(listaEjercicios);
        listEjerciciosRutina.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listEjerciciosRutina.setCellFactory(param -> new javafx.scene.control.ListCell<Ejercicio>() {
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

        // Cargar coaches
        List<Coach> coaches = usuarioDAO.listar().stream()
                .filter(u -> u instanceof Coach)
                .map(u -> (Coach) u)
                .collect(Collectors.toList());
        listaCoaches.setAll(coaches);
        cbCoach.setItems(listaCoaches);
        cbCoach.setCellFactory(param -> new javafx.scene.control.ListCell<Coach>() {
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

        cbCoach.setButtonCell(new javafx.scene.control.ListCell<Coach>() {
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

        // Configuración columnas tabla
        tcIdRutina.setCellValueFactory(r -> new SimpleStringProperty(r.getValue().getId() == null ? "" : String.valueOf(r.getValue().getId())));
        tcTituloRutina.setCellValueFactory(r -> new SimpleStringProperty(safeString(r.getValue().getTitulo())));
        tcDuracion.setCellValueFactory(r -> new SimpleStringProperty(r.getValue().getDuracionEstimada() == null ? "" : String.valueOf(r.getValue().getDuracionEstimada())));
        tcDificultadRutina.setCellValueFactory(r -> new SimpleStringProperty(
                r.getValue().getNivelDificultad() == null ? "" : r.getValue().getNivelDificultad().name()
        ));
        tcCoachRutina.setCellValueFactory(r -> {
            Integer idCoach = r.getValue().getIdCoach();
            if (idCoach == null) return new SimpleStringProperty("");
            Usuario coach = usuarioDAO.listar().stream().filter(u -> {
                try {
                    Object idObj = u.getClass().getMethod("getId").invoke(u);
                    return idObj != null && Integer.valueOf(String.valueOf(idObj)).equals(idCoach);
                } catch (Exception e) {
                    return false;
                }
            }).findFirst().orElse(null);
            if (coach == null) return new SimpleStringProperty("");
            return new SimpleStringProperty(coach.getNombres() + " " + coach.getApellidos());
        });

        // Cargar datos
        refreshTabla();

        // Selección en tabla
        tableRutina.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) cargarRutinaEnFormulario(newSel);
        });

        // FILTRADO
        FilteredList<Rutina> filteredData = new FilteredList<>(listaRutinas, p -> true);
        txtFiltrarRutina.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(rut -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String filtro = newVal.toLowerCase();
                if (safeString(rut.getTitulo()).toLowerCase().contains(filtro)) return true;
                if (safeString(rut.getDescripcion()).toLowerCase().contains(filtro)) return true;
                if (rut.getId() != null && String.valueOf(rut.getId()).contains(filtro)) return true;
                return false;
            });
        });
        SortedList<Rutina> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableRutina.comparatorProperty());
        tableRutina.setItems(sortedData);
    }

    // ================= CRUD =================
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
            try {
                Object idObj = coachSel.getClass().getMethod("getId").invoke(coachSel);
                if (idObj != null) r.setIdCoach(Integer.valueOf(String.valueOf(idObj)));
            } catch (Exception ignored) {}
        }

        // Asociar ejercicios seleccionados
        List<Ejercicio> seleccionados = new ArrayList<>(listEjerciciosRutina.getSelectionModel().getSelectedItems());
        r.setEjercicios(seleccionados);

        rutinaDAO.crear(r);
        refreshTabla();
        limpiarCampos();
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
            try {
                Object idObj = coachSel.getClass().getMethod("getId").invoke(coachSel);
                if (idObj != null) sel.setIdCoach(Integer.valueOf(String.valueOf(idObj)));
            } catch (Exception ignored) {}
        }

        // actualizar ejercicios asociados
        List<Ejercicio> seleccionados = new ArrayList<>(listEjerciciosRutina.getSelectionModel().getSelectedItems());
        sel.setEjercicios(seleccionados);

        boolean ok = rutinaDAO.actualizar(sel);
        if (!ok) mostrarAlerta("No fue posible actualizar la rutina.");
        refreshTabla();
    }

    @FXML
    void onEliminararRutina(ActionEvent event) {
        Rutina sel = tableRutina.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta("Seleccione una rutina para eliminar.");
            return;
        }
        rutinaDAO.eliminar(sel);
        refreshTabla();
        limpiarCampos();
    }

    // ================ Helpers =================
    private void refreshTabla() {
        listaRutinas.setAll(rutinaDAO.listar());
    }

    private void cargarRutinaEnFormulario(Rutina r) {
        if (r == null) return;
        txtTituloRutina.setText(safeString(r.getTitulo()));
        txtDescripcionRutina.setText(safeString(r.getDescripcion()));
        txtDuracionRutina.setText(r.getDuracionEstimada() == null ? "" : String.valueOf(r.getDuracionEstimada()));
        cbDificultadRutina.setValue(r.getNivelDificultad());

        // seleccionar coach
        if (r.getIdCoach() != null) {
            Coach coach = listaCoaches.stream().filter(c -> {
                try {
                    Object idObj = c.getClass().getMethod("getId").invoke(c);
                    return idObj != null && Integer.valueOf(String.valueOf(idObj)).equals(r.getIdCoach());
                } catch (Exception e) {
                    return false;
                }
            }).findFirst().orElse(null);
            cbCoach.setValue(coach);
        } else {
            cbCoach.setValue(null);
        }

        // seleccionar ejercicios asociados
        listEjerciciosRutina.getSelectionModel().clearSelection();
        if (r.getEjercicios() != null) {
            for (Ejercicio ej : r.getEjercicios()) {
                listEjerciciosRutina.getSelectionModel().select(ej);
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
