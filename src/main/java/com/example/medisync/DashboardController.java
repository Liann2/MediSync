package com.example.medisync;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class DashboardController {

    private Stage stage;
    private Scene scene;
    private Parent root;
    private AnchorPane scenePane;


    public void logoutUser(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("You are about to Logout!");
        alert.setContentText("Are you sure you want to Logout?");

        if (alert.showAndWait().get() == ButtonType.OK){
            root = FXMLLoader.load(getClass().getResource("Login.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }
    public void switchToAppointScheduler(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("AppointmentScheduler.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToRegisterPatient(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("RegisterPatient.fxml"));
//        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("RegisterPatient.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToPatientQueue(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("PatientQueue.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
