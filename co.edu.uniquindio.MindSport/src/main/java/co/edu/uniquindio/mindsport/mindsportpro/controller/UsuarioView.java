package co.edu.uniquindio.mindsport.mindsportpro.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class UsuarioView {

    public static void show(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(UsuarioView.class.getResource("/co/edu/uniquindio/mindsport/mindsportpro/Usuario.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 700, 720);
        stage.setTitle("Gestión de Usuarios - MindSport Pro");
        stage.setScene(scene);
        stage.show();
    }

}


