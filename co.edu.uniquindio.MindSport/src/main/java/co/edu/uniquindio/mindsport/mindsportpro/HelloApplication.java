package co.edu.uniquindio.mindsport.mindsportpro;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Login.fxml"));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root, 450, 350);
        stage.setTitle("MindSport Pro - Login");
        stage.setScene(scene);
        stage.show();

        System.out.println("✅ Aplicación iniciada - Pantalla de Login");
    }

    public static void main(String[] args) {
        launch();
    }
}