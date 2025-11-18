package co.edu.uniquindio.mindsport.mindsportpro.controller;

import co.edu.uniquindio.mindsport.mindsportpro.dao.UsuarioDAOJdbc;
import co.edu.uniquindio.mindsport.mindsportpro.model.Usuario;
import co.edu.uniquindio.mindsport.mindsportpro.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class LoginController {

    @FXML
    private TextField txtCedula;

    @FXML
    private PasswordField txtContrasena;

    @FXML
    private Button btnLogin;

    @FXML
    private Label lblError;

    private final UsuarioDAOJdbc usuarioDAO = UsuarioDAOJdbc.getInstancia();

    @FXML
    void onLogin(ActionEvent event) {
        String cedula = txtCedula.getText().trim();
        String contrasena = txtContrasena.getText();

        // Validar campos vacíos
        if (cedula.isEmpty() || contrasena.isEmpty()) {
            mostrarError("Por favor complete todos los campos");
            return;
        }

        // Autenticar usuario
        Optional<Usuario> usuarioOpt = usuarioDAO.autenticar(cedula, contrasena);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            
            // Guardar sesión
            SessionManager.getInstance().login(usuario, stage);
            
            // Navegar al menú principal
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uniquindio/mindsport/mindsportpro/MenuPrincipal.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, 600, 500);
                stage.setScene(scene);
                stage.setTitle("Menú Principal - MindSport Pro");
                
                System.out.println("✓ Usuario logueado: " + usuario.getNombres() + " - Rol: " + usuario.getRol());
            } catch (IOException e) {
                mostrarError("Error al cargar menú principal");
                e.printStackTrace();
            }
            
        } else {
            mostrarError("Cédula o contraseña incorrecta");
        }
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
        lblError.setStyle("-fx-text-fill: red;");
    }

    @FXML
    void initialize() {
        System.out.println("🔐 LoginController inicializado");
    }

}

