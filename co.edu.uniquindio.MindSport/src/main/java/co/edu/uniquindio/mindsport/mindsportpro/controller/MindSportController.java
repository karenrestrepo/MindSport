package co.edu.uniquindio.mindsport.mindsportpro.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class MindSportController {

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab tabUsuarios;

    @FXML
    private Tab tabRutinas;

    @FXML
    private Tab tabEjercicios;

    @FXML
    private Tab tabTecnicas;


    // Controladores inyectados automáticamente por JavaFX cuando usa fx:id en fx:include
    @FXML
    private UsuarioController usuarioController;

    @FXML
    private RutinaController rutinaController;

    @FXML
    private EjercicioController ejercicioController;

    @FXML
    private TecnicaController tecnicaController;

    @FXML
    void initialize() {
        System.out.println("🚀 Inicializando MindSportController (Controlador Central)...");

        // Conectar controladores hijos al principal
        conectarControladores();

        // Listener para detectar cambios de pestaña
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                System.out.println("📑 Cambiando a pestaña: " + newTab.getText());
                onTabChanged(newTab);
            }
        });

        System.out.println("✅ MindSportController inicializado correctamente");
    }

    private void conectarControladores() {
        if (usuarioController != null) {
            usuarioController.setControladorPrincipal(this);
            System.out.println("✓ UsuarioController conectado");
        } else {
            System.err.println("⚠️ UsuarioController no encontrado");
        }

        if (rutinaController != null) {
            rutinaController.setControladorPrincipal(this);
            System.out.println("✓ RutinaController conectado");
        } else {
            System.err.println("⚠️ RutinaController no encontrado");
        }

        if (ejercicioController != null) {
            ejercicioController.setControladorPrincipal(this);
            System.out.println("✓ EjercicioController conectado");
        } else {
            System.err.println("⚠️ EjercicioController no encontrado");
        }

        if (tecnicaController != null) {
            System.out.println("✓ TecnicaController encontrado");
        }
    }

    private void onTabChanged(Tab tab) {
        String tabText = tab.getText();

        // Refrescar la pestaña actual cargando datos frescos
        if (tabText.equals("Rutinas")) {
            System.out.println("🔄 Sincronizando datos para Rutinas...");
            notificarCambiosARutinas();
        } else if (tabText.equals("Ejercicios")) {
            System.out.println("🔄 Sincronizando datos para Ejercicios...");
            notificarCambiosAEjercicios();
        } else if (tabText.equals("Gestión de usuarios")) {
            System.out.println("🔄 Sincronizando datos para Usuarios...");
            notificarCambiosAUsuarios();
        }
    }

    // Métodos de notificación cuando hay cambios
    public void notificarCambioUsuario() {
        System.out.println("📢 Notificando cambio en Usuarios a todos los controladores...");
        if (rutinaController != null) {
            rutinaController.actualizarDatosExternos();
        }
    }

    public void notificarCambioEjercicio() {
        System.out.println("📢 Notificando cambio en Ejercicios a todos los controladores...");
        if (rutinaController != null) {
            rutinaController.actualizarDatosExternos();
        }
    }

    public void notificarCambioRutina() {
        System.out.println("📢 Notificando cambio en Rutinas...");
        // Si hubiera otros controladores que dependen de rutinas, notificarlos aquí
    }

    // Métodos privados para refrescar cada controlador cuando se cambia a su pestaña
    private void notificarCambiosAUsuarios() {
        if (usuarioController != null) {
            usuarioController.refrescarDatos();
        }
    }

    private void notificarCambiosARutinas() {
        if (rutinaController != null) {
            rutinaController.refrescarDatos();
        }
    }

    private void notificarCambiosAEjercicios() {
        if (ejercicioController != null) {
            ejercicioController.refrescarDatos();
        }
    }
}
