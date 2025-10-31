package co.edu.uniquindio.mindsport.mindsportpro.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

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
    private Tab tabSesiones;

    // Controladores inyectados
    @FXML
    private UsuarioController usuarioController;

    @FXML
    private RutinaController rutinaController;

    @FXML
    private EjercicioController ejercicioController;

    @FXML
    private SesionController sesionController;

    // Flags para carga lazy
    private boolean usuariosCargados = false;
    private boolean rutinasCargadas = false;
    private boolean ejerciciosCargados = false;
    private boolean sesionesCargadas = false;

    // Flags para cambios pendientes
    private boolean cambiosUsuarios = false;
    private boolean cambiosEjercicios = false;
    private boolean cambiosRutinas = false;

    @FXML
    void initialize() {
        System.out.println("🚀 Inicializando MindSportController...");

        // Conectar controladores
        conectarControladores();

        // Listener para cambios de pestaña
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
        }

        if (rutinaController != null) {
            rutinaController.setControladorPrincipal(this);
            System.out.println("✓ RutinaController conectado");
        }

        if (ejercicioController != null) {
            ejercicioController.setControladorPrincipal(this);
            System.out.println("✓ EjercicioController conectado");
        }

        if (sesionController != null) {
            sesionController.setControladorPrincipal(this);
            System.out.println("✓ SesionController conectado");
        }
    }

    private void onTabChanged(Tab tab) {
        String tabText = tab.getText();

        if (tabText.equals("Gestión de usuarios")) {
            if (!usuariosCargados) {
                cargarDatosAsync(() -> {
                    if (usuarioController != null) {
                        usuarioController.refrescarDatos();
                    }
                    usuariosCargados = true;
                }, "Usuarios");
            }
        } else if (tabText.equals("Rutinas")) {
            if (!rutinasCargadas || cambiosUsuarios || cambiosEjercicios) {
                cargarDatosAsync(() -> {
                    if (rutinaController != null) {
                        rutinaController.actualizarDatosExternos();
                    }
                    rutinasCargadas = true;
                    cambiosUsuarios = false;
                    cambiosEjercicios = false;
                }, "Rutinas");
            }
        } else if (tabText.equals("Ejercicios")) {
            if (!ejerciciosCargados) {
                cargarDatosAsync(() -> {
                    if (ejercicioController != null) {
                        ejercicioController.refrescarDatos();
                    }
                    ejerciciosCargados = true;
                }, "Ejercicios");
            }
        } else if (tabText.equals("Sesiones")) {
            if (!sesionesCargadas || cambiosUsuarios || cambiosRutinas) {
                cargarDatosAsync(() -> {
                    if (sesionController != null) {
                        sesionController.actualizarDatosExternos();
                    }
                    sesionesCargadas = true;
                    cambiosUsuarios = false;
                    cambiosRutinas = false;
                }, "Sesiones");
            }
        }
    }

    /**
     * Carga datos en segundo plano mostrando cursor de espera
     * @param cargaTask Tarea de carga a ejecutar
     * @param nombreSeccion Nombre de la sección para logging
     */
    private void cargarDatosAsync(Runnable cargaTask, String nombreSeccion) {
        // Cambiar cursor a "espera"
        if (tabPane.getScene() != null) {
            tabPane.getScene().setCursor(Cursor.WAIT);
        }

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                System.out.println("⏳ Cargando " + nombreSeccion + "...");
                cargaTask.run();
                return null;
            }

            @Override
            protected void succeeded() {
                // Restaurar cursor normal
                Platform.runLater(() -> {
                    if (tabPane.getScene() != null) {
                        tabPane.getScene().setCursor(Cursor.DEFAULT);
                    }
                    System.out.println("✅ " + nombreSeccion + " cargados correctamente");
                });
            }

            @Override
            protected void failed() {
                // Restaurar cursor normal incluso si hay error
                Platform.runLater(() -> {
                    if (tabPane.getScene() != null) {
                        tabPane.getScene().setCursor(Cursor.DEFAULT);
                    }
                    System.err.println("❌ Error al cargar " + nombreSeccion + ": " + getException().getMessage());
                    getException().printStackTrace();
                });
            }
        };

        // Ejecutar en un thread separado
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    // ======================================================
    // MÉTODOS DE NOTIFICACIÓN
    // ======================================================

    public void notificarCambioUsuario() {
        System.out.println("📢 Marcando cambios en Usuarios...");
        cambiosUsuarios = true;
    }

    public void notificarCambioEjercicio() {
        System.out.println("📢 Marcando cambios en Ejercicios...");
        cambiosEjercicios = true;
    }

    public void notificarCambioRutina() {
        System.out.println("📢 Marcando cambios en Rutinas...");
        cambiosRutinas = true;
    }
}