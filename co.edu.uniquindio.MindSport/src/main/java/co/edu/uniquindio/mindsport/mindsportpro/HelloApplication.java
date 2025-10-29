package co.edu.uniquindio.mindsport.mindsportpro;

import co.edu.uniquindio.mindsport.mindsportpro.controller.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("MindSport.fxml"));
        Parent root = fxmlLoader.load();

        // Obtener el controlador principal
        MindSportController mainController = fxmlLoader.getController();

        // Obtener referencias a los controladores hijos a través del root
        conectarControladores(root, mainController);

        Scene scene = new Scene(root, 700, 720);
        stage.setTitle("Mind Sport Pro");
        stage.setScene(scene);
        stage.show();

        System.out.println("✅ Aplicación iniciada correctamente");
    }

    private void conectarControladores(Parent root, MindSportController mainController) {
        try {
            // Buscar los AnchorPane de cada pestaña y obtener sus controladores
            // Nota: Los controladores ya fueron creados por fx:include en el FXML

            // Esta es una forma alternativa: acceder a los nodos por ID si están definidos
            // Por ahora, los controladores se conectarán automáticamente cuando se inicialicen

            System.out.println("🔌 Intentando conectar controladores...");

            // Los controladores se auto-registrarán cuando MindSport.fxml cargue los fx:include
            // y llame a sus métodos initialize()

        } catch (Exception e) {
            System.err.println("⚠️ Error al conectar controladores: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch();
    }
}