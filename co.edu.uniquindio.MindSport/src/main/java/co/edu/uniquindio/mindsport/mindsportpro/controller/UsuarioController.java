package co.edu.uniquindio.mindsport.mindsportpro.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import co.edu.uniquindio.mindsport.mindsportpro.dao.UsuarioDAOJdbc;
import co.edu.uniquindio.mindsport.mindsportpro.enums.Genero;
import co.edu.uniquindio.mindsport.mindsportpro.enums.Rol;
import co.edu.uniquindio.mindsport.mindsportpro.model.Atleta;
import co.edu.uniquindio.mindsport.mindsportpro.model.Coach;
import co.edu.uniquindio.mindsport.mindsportpro.model.Usuario;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class UsuarioController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnActualizarUsuario;

    @FXML
    private Button btnAgregarUsuario;

    @FXML
    private Button btnEliminarUsuario;

    @FXML
    private ComboBox<Genero> cbGenero;

    @FXML
    private ComboBox<Rol> cbRol;

    @FXML
    private DatePicker datepFechaNacimiento;

    @FXML
    private TableView<Usuario> tableUsuario;

    @FXML
    private TableColumn<Usuario, String> tcCedulaUsuario;

    @FXML
    private TableColumn<Usuario, String> tcCorreoUsuario;

    @FXML
    private TableColumn<Usuario, String> tcGeneroUsuario;

    @FXML
    private TableColumn<Usuario, String> tcNombreUsuario;

    @FXML
    private TableColumn<Usuario, String> tcRolUsuario;

    @FXML
    private TextField txtAltura;

    @FXML
    private TextField txtApellidoUsuario;

    @FXML
    private TextField txtCedulaUsuario;

    @FXML
    private TextField txtCentroTrabajo;

    @FXML
    private PasswordField txtContraseñaUsuario;

    @FXML
    private TextField txtCorreoUsuario;

    @FXML
    private TextField txtDisponibilidad;

    @FXML
    private TextField txtEspecialidad;

    @FXML
    private TextField txtFiltrarUsuario;

    @FXML
    private TextField txtIdProfesional;

    @FXML
    private TextField txtNombreUsuario;

    @FXML
    private TextField txtPerfilDeportivo;

    @FXML
    private TextField txtPeso;

    @FXML
    private TextField txtTelefono2Usuario;

    @FXML
    private TextField txtTelefonoUsuario;

    @FXML
    private VBox vboxAtleta;

    @FXML
    private VBox vboxCoach;
    private final UsuarioDAOJdbc usuarioDAO = UsuarioDAOJdbc.getInstancia();
    private final ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();

    @FXML
    void onActualizarUsuario(ActionEvent event) {
        Usuario seleccionado = tableUsuario.getSelectionModel().getSelectedItem();
        if (seleccionado == null) { mostrarAlerta("Seleccione un usuario para actualizar."); return; }

        // construir objeto actualizado (podrías actualizar el mismo objeto)
        // para simplicidad actualizamos el objeto seleccionado directamente
        try {
            seleccionado.setNombres(txtNombreUsuario.getText().trim());
            seleccionado.setApellidos(txtApellidoUsuario.getText().trim());
            seleccionado.setCedula(txtCedulaUsuario.getText().trim());
            seleccionado.setCorreo(txtCorreoUsuario.getText().trim());
            seleccionado.setContrasena(txtContraseñaUsuario.getText().trim());
            // genero
            tryInvokeSetter(seleccionado, "setGenero", new Class[]{Genero.class}, new Object[]{cbGenero.getValue()});
            // telefonos
            List<String> telefonos = new ArrayList<>();
            if (!txtTelefonoUsuario.getText().trim().isEmpty()) telefonos.add(txtTelefonoUsuario.getText().trim());
            if (!txtTelefono2Usuario.getText().trim().isEmpty()) telefonos.add(txtTelefono2Usuario.getText().trim());
            tryInvokeSetter(seleccionado, "setTelefonos", new Class[]{List.class}, new Object[]{telefonos});

            // si es Atleta actualizamos campos propios
            if (seleccionado instanceof Atleta) {
                Atleta a = (Atleta) seleccionado;
                a.setPerfilDeportivo(txtPerfilDeportivo.getText().trim());
                a.setFechaNacimiento(datepFechaNacimiento.getValue());
                a.setPeso(parseDoubleToDouble(txtPeso.getText()));
                a.setAltura(parseDoubleToDouble(txtAltura.getText()));
            } else if (seleccionado instanceof Coach) {
                Coach c = (Coach) seleccionado;
                c.setIdProfesional(txtIdProfesional.getText().trim());
                c.setEspecialidad(txtEspecialidad.getText().trim());
                c.setCentroTrabajo(txtCentroTrabajo.getText().trim());
                c.setDisponibilidad(txtDisponibilidad.getText().trim());
            }

            usuarioDAO.actualizar(seleccionado);
            refreshTabla();

            if (controladorPrincipal != null) {
                controladorPrincipal.notificarCambioUsuario();
            }


        } catch (Exception ex) {
            mostrarAlerta("No fue posible actualizar: " + ex.getMessage());
        }

    }

    @FXML
    void onAgregarUsuario(ActionEvent event) {
        Rol rol = cbRol.getValue();

        if (rol == null) {
            mostrarAlerta("Debe seleccionar un rol antes de agregar.");
            return;
        }

        // Campos comunes
        String nombres = txtNombreUsuario.getText();
        String apellidos = txtApellidoUsuario.getText();
        String cedula = txtCedulaUsuario.getText();
        String correo = txtCorreoUsuario.getText();
        String contrasena = txtContraseñaUsuario.getText();
        Genero genero = cbGenero.getValue();
        String telefono1 = txtTelefonoUsuario.getText();
        String telefono2 = txtTelefono2Usuario.getText();

        List<String> telefonos = new ArrayList<>();
        if (!txtTelefonoUsuario.getText().trim().isEmpty()) telefonos.add(txtTelefonoUsuario.getText().trim());
        if (!txtTelefono2Usuario.getText().trim().isEmpty()) telefonos.add(txtTelefono2Usuario.getText().trim());

        if (nombres.isEmpty() || apellidos.isEmpty() || cedula.isEmpty()) {
            mostrarAlerta("Complete nombre, apellidos y cédula.");
            return;
        }

        Usuario u;
        if (rol == Rol.ATLETA) {
            Atleta a = new Atleta();
            a.setNombres(nombres);
            a.setApellidos(apellidos);
            a.setCedula(cedula);
            a.setCorreo(correo);
            a.setContrasena(contrasena);
            // genero como enum
            tryInvokeSetter(a, "setGenero", new Class[]{Genero.class}, new Object[]{genero});
            a.setTelefonos(telefonos);
            a.setPerfilDeportivo(txtPerfilDeportivo.getText().trim());
            a.setFechaNacimiento(datepFechaNacimiento.getValue());
            a.setPeso(parseDoubleToDouble(txtPeso.getText()));
            a.setAltura(parseDoubleToDouble(txtAltura.getText()));
            tryInvokeSetter(a, "setRol", new Class[]{Rol.class}, new Object[]{Rol.ATLETA});
            u = a;
        } else {
            Coach c = new Coach();
            c.setNombres(nombres);
            c.setApellidos(apellidos);
            c.setCedula(cedula);
            c.setCorreo(correo);
            c.setContrasena(contrasena);
            tryInvokeSetter(c, "setGenero", new Class[]{Genero.class}, new Object[]{genero});
            c.setTelefonos(telefonos);
            c.setIdProfesional(txtIdProfesional.getText().trim());
            c.setEspecialidad(txtEspecialidad.getText().trim());
            c.setCentroTrabajo(txtCentroTrabajo.getText().trim());
            c.setDisponibilidad(txtDisponibilidad.getText().trim());
            tryInvokeSetter(c, "setRol", new Class[]{Rol.class}, new Object[]{Rol.COACH});
            u = c;
        }

        usuarioDAO.crear(u);
        refreshTabla();
        limpiarCampos();

        if (controladorPrincipal != null) {
            controladorPrincipal.notificarCambioUsuario();
        }

    }

    @FXML
    void onEliminararUsuario(ActionEvent event) {
        Usuario sel = tableUsuario.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarAlerta("Seleccione un usuario para eliminar."); return; }
        usuarioDAO.eliminar(sel);
        refreshTabla();
        limpiarCampos();

        if (controladorPrincipal != null) {
            controladorPrincipal.notificarCambioUsuario();
        }
    }

    @FXML
    void onGenero(ActionEvent event) {

    }

    @FXML
    void onRol(ActionEvent event) {
        Rol rolSeleccionado = cbRol.getValue();

        if (rolSeleccionado == null) {
            ocultarAmbos();
            return;
        }
        if (rolSeleccionado == Rol.ATLETA) mostrarAtleta();
        else if (rolSeleccionado == Rol.COACH) mostrarCoach();
        else ocultarAmbos();

    }

    @FXML
    void initialize() {
        cbGenero.setItems(FXCollections.observableArrayList(Genero.values()));
        cbRol.setItems(FXCollections.observableArrayList(Rol.values()));

        // Configurar columnas de tabla
        tcNombreUsuario.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getNombres() + " " + cellData.getValue().getApellidos()));
        tcCedulaUsuario.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCedula()));
        tcCorreoUsuario.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCorreo()));
        tcGeneroUsuario.setCellValueFactory(data -> {
            Genero g = data.getValue().getGenero();
            String s = (g != null) ? g.name() : "";
            return new SimpleStringProperty(s);
        });

        tcRolUsuario.setCellValueFactory(data -> {
            Rol r = data.getValue().getRol();
            String s = (r != null) ? r.name() : "";
            return new SimpleStringProperty(s);
        });

        refreshTabla();
        FilteredList<Usuario> filteredData = new FilteredList<>(listaUsuarios, p -> true);

        txtFiltrarUsuario.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(usuario -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String filtro = newValue.toLowerCase();

                // Coincidencias con nombre, apellido, cédula o correo
                if (usuario.getNombres().toLowerCase().contains(filtro)) return true;
                if (usuario.getApellidos().toLowerCase().contains(filtro)) return true;
                if (usuario.getCedula().toLowerCase().contains(filtro)) return true;
                if (usuario.getCorreo().toLowerCase().contains(filtro)) return true;

                return false;
            });
        });

        SortedList<Usuario> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableUsuario.comparatorProperty());
        tableUsuario.setItems(sortedData);
        tableUsuario.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) cargarUsuarioEnFormulario(newSel);
        });
    }

    private void limpiarCampos() {
        txtNombreUsuario.clear();
        txtApellidoUsuario.clear();
        txtCedulaUsuario.clear();
        txtCorreoUsuario.clear();
        txtContraseñaUsuario.clear();
        txtTelefonoUsuario.clear();
        txtTelefono2Usuario.clear();
        txtPerfilDeportivo.clear();
        txtPeso.clear();
        txtAltura.clear();
        datepFechaNacimiento.setValue(null);
        txtIdProfesional.clear();
        txtEspecialidad.clear();
        txtCentroTrabajo.clear();
        txtDisponibilidad.clear();
        cbRol.setValue(null);
        cbGenero.setValue(null);
        ocultarAmbos();
    }

    private void refreshTabla() {
        listaUsuarios.setAll(usuarioDAO.listar());
        tableUsuario.setItems(listaUsuarios);
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private Double parseDoubleToDouble(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            return Double.valueOf(s.replace(",", "."));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void ocultarAmbos() {
        vboxAtleta.setVisible(false);
        vboxAtleta.setManaged(false);
        vboxCoach.setVisible(false);
        vboxCoach.setManaged(false);
    }

    private void mostrarAtleta() {
        vboxAtleta.setVisible(true);
        vboxAtleta.setManaged(true);
        vboxCoach.setVisible(false);
        vboxCoach.setManaged(false);
    }

    private void mostrarCoach() {
        vboxCoach.setVisible(true);
        vboxCoach.setManaged(true);
        vboxAtleta.setVisible(false);
        vboxAtleta.setManaged(false);
    }

    private void cargarUsuarioEnFormulario(Usuario u) {
        if (u == null) return;
        txtNombreUsuario.setText(safeString(u.getNombres()));
        txtApellidoUsuario.setText(safeString(u.getApellidos()));
        txtCedulaUsuario.setText(safeString(u.getCedula()));
        txtCorreoUsuario.setText(safeString(u.getCorreo()));
        txtContraseñaUsuario.setText(safeString(u.getContrasena()));
        Object g = tryInvokeGetter(u, "getGenero");
        if (g instanceof Genero) cbGenero.setValue((Genero) g);
        else cbGenero.setValue(null);

        // telefonos
        Object t = tryInvokeGetter(u, "getTelefonos");
        if (t instanceof List) {
            List<?> lista = (List<?>) t;
            if (lista.size() > 0) txtTelefonoUsuario.setText(String.valueOf(lista.get(0)));
            if (lista.size() > 1) txtTelefono2Usuario.setText(String.valueOf(lista.get(1)));
        }

        if (u instanceof Atleta) {
            Atleta a = (Atleta) u;
            txtPerfilDeportivo.setText(safeString(a.getPerfilDeportivo()));
            txtPeso.setText(a.getPeso() != null ? String.valueOf(a.getPeso()) : "");
            txtAltura.setText(a.getAltura() != null ? String.valueOf(a.getAltura()) : "");
            datepFechaNacimiento.setValue(a.getFechaNacimiento());
            cbRol.setValue(Rol.ATLETA);
            mostrarAtleta();
        } else if (u instanceof Coach) {
            Coach c = (Coach) u;
            txtIdProfesional.setText(safeString(c.getIdProfesional()));
            txtEspecialidad.setText(safeString(c.getEspecialidad()));
            txtCentroTrabajo.setText(safeString(c.getCentroTrabajo()));
            txtDisponibilidad.setText(safeString(c.getDisponibilidad()));
            cbRol.setValue(Rol.COACH);
            mostrarCoach();
        } else {
            ocultarAmbos();
        }
    }


    private String safeString(String s) {
        return (s == null) ? "" : s;
    }


    private Object tryInvokeGetter(Object target, String methodName) {
        try {
            java.lang.reflect.Method m = target.getClass().getMethod(methodName);
            return m.invoke(target);
        } catch (Exception e) { return null; }
    }

    private boolean tryInvokeSetter(Object target, String methodName, Class[] paramTypes, Object[] args) {
        try {
            java.lang.reflect.Method m = target.getClass().getMethod(methodName, paramTypes);
            m.invoke(target, args);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private MindSportController controladorPrincipal;

    // Método público para conectar
    public void setControladorPrincipal(MindSportController controlador) {
        this.controladorPrincipal = controlador;
        System.out.println("🔗 UsuarioController conectado al controlador principal");
    }

    // Método para refrescar datos cuando se cambia a esta pestaña
    public void refrescarDatos() {
        System.out.println("🔄 Refrescando datos en UsuarioController...");
        refreshTabla();
    }

}

