package com.example.medisync;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Login extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

            Parent root = FXMLLoader.load(getClass().getResource("LogIn.fxml"));
            Scene scene = new Scene(root, 1280, 960);

        Image icon = new Image(getClass().getResourceAsStream("/com/example/medisync/Images/MediSyncIcon.png"));
            primaryStage.getIcons().add(icon);
            primaryStage.setTitle("MediSync Hospital Queue Management System");
            primaryStage.setScene(scene);
            primaryStage.show();

    }
}
