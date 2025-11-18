package co.edu.uniquindio.mindsport.mindsportpro.controller;

import co.edu.uniquindio.mindsport.mindsportpro.model.Usuario;
import co.edu.uniquindio.mindsport.mindsportpro.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuPrincipalController {

    @FXML
    private Label lblBienvenida;

    @FXML
    private VBox vboxOpciones;

    @FXML
    private Button btnCerrarSesion;

    @FXML
    void initialize() {
        Usuario usuario = SessionManager.getInstance().getUsuarioActual();

        if (usuario == null) {
            System.err.println("Error: No hay usuario en sesion");
            return;
        }

        lblBienvenida.setText("Bienvenido, " + usuario.getNombres() + " " + usuario.getApellidos());
        cargarOpcionesSegunRol(usuario.getRol());
        System.out.println("Menu Principal cargado para rol: " + usuario.getRol());
    }

    private void cargarOpcionesSegunRol(Integer rol) {
        vboxOpciones.getChildren().clear();

        if (rol == null) {
            System.err.println("Usuario sin rol definido");
            return;
        }

        if (rol == 2) { // COACH - ve todo
            agregarBoton("Gestion de Usuarios", this::abrirUsuarios);
            agregarBoton("Gestion de Rutinas", this::abrirRutinas);
            agregarBoton("Gestion de Ejercicios", this::abrirEjercicios);
            agregarBoton("Gestion de Sesiones", this::abrirSesiones);
            agregarBoton("Reporte Rutinas Mas Usadas", this::abrirReporteRutinas);
            agregarBoton("Composicion de Rutinas", this::abrirReporteRutinasDetalle);
            agregarBoton("Actividad Mensual", this::abrirActividadMensual);
        } else if (rol == 1) { // ATLETA
            agregarBoton("Mis Sesiones", this::abrirSesiones);
            agregarBoton("Mi Reporte Resumen", this::abrirReporteResumen);
            agregarBoton("Reporte Rutinas Mas Usadas", this::abrirReporteRutinas);
            agregarBoton("Composicion de Rutinas", this::abrirReporteRutinasDetalle);
        }
    }

    private void agregarBoton(String texto, Runnable accion) {
        Button btn = new Button(texto);
        btn.setPrefWidth(250.0);
        btn.setPrefHeight(40.0);
        btn.setStyle("-fx-font-size: 14px;");
        btn.setOnAction(event -> accion.run());
        vboxOpciones.getChildren().add(btn);
    }

    private void abrirUsuarios() {
        navegarA("Usuario.fxml", "Gestion de Usuarios");
    }

    private void abrirRutinas() {
        navegarA("Rutina.fxml", "Gestion de Rutinas");
    }

    private void abrirEjercicios() {
        navegarA("Ejercicio.fxml", "Gestion de Ejercicios");
    }

    private void abrirSesiones() {
        navegarA("Sesion.fxml", "Gestion de Sesiones");
    }

    private void abrirReporteResumen() {
        navegarA("ReporteResumen.fxml", "Reporte Resumen");
    }

    private void abrirReporteRutinas() {
        navegarA("ReporteRutinas.fxml", "Reporte Rutinas Mas Usadas");
    }

    private void abrirReporteRutinasDetalle() {
        navegarA("ReporteRutinasDetalle.fxml", "Composicion Detallada de Rutinas");
    }

    private void abrirActividadMensual() {
        navegarA("ReporteActividadMensual.fxml", "Actividad Mensual");
    }

    private void navegarA(String fxml, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uniquindio/mindsport/mindsportpro/" + fxml));
            Parent root = loader.load();

            Stage stage = SessionManager.getInstance().getStagePrincipal();
            if (stage == null) {
                System.err.println("Error: Stage principal no encontrado en SessionManager");
                return;
            }
            Scene scene = new Scene(root, 700, 720);
            stage.setScene(scene);
            stage.setTitle(titulo + " - MindSport Pro");

            System.out.println("Navegando a: " + titulo);
        } catch (IOException e) {
            System.err.println("Error al cargar " + fxml + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void onCerrarSesion(ActionEvent event) {
        try {
            SessionManager.getInstance().logout();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uniquindio/mindsport/mindsportpro/Login.fxml"));
            Parent root = loader.load();

            Stage stage = SessionManager.getInstance().getStagePrincipal();
            Scene scene = new Scene(root, 450, 350);
            stage.setScene(scene);
            stage.setTitle("MindSport Pro - Login");

            System.out.println("Sesion cerrada");
        } catch (IOException e) {
            System.err.println("Error al cerrar sesion: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
