package co.edu.uniquindio.mindsport.mindsportpro.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SesionView {

    public static void show(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(SesionView.class.getResource("/co/edu/uniquindio/mindsport/mindsportpro/Sesion.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 700, 720);
        stage.setTitle("Gestión de Sesiones - MindSport Pro");
        stage.setScene(scene);
        stage.show();
    }

}


