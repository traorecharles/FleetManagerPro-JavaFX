package com.miage.fleet;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class FleetApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        showLogin();
        stage.setTitle("FleetManager Pro — Gestion du Parc Automobile");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    public static void showLogin() throws IOException {
        URL fxmlUrl = FleetApp.class.getResource("/com/miage/fleet/fxml/login.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Scene scene = new Scene(loader.load(), 1280, 800);
        applyStylesheet(scene);
        primaryStage.setScene(scene);
    }

    public static void showMain() throws IOException {
        URL fxmlUrl = FleetApp.class.getResource("/com/miage/fleet/fxml/main.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Scene scene = new Scene(loader.load(), 1400, 850);
        applyStylesheet(scene);
        primaryStage.setScene(scene);
    }

    private static void applyStylesheet(Scene scene) {
        URL cssUrl = FleetApp.class.getResource("/com/miage/fleet/css/styles.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }
    }

    public static Stage getPrimaryStage() { return primaryStage; }

    public static void main(String[] args) {
        launch();
    }
}
