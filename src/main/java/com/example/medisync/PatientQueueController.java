package com.example.medisync;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class PatientQueueController{

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private AnchorPane draggablePane;
    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    public void initialize() {
        makeDraggable(draggablePane);
    }

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

    public void switchToAppointmentScheduler(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("AppointmentScheduler.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToDashboard(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToRegisterPatient(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("RegisterPatient.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void makeDraggable(AnchorPane pane) {
        pane.setOnMousePressed(event -> {
            xOffset = event.getSceneX() - pane.getLayoutX();
            yOffset = event.getSceneY() - pane.getLayoutY();
        });

        pane.setOnMouseDragged(event -> {
            pane.setLayoutX(event.getSceneX() - xOffset);
            pane.setLayoutY(event.getSceneY() - yOffset);
        });
    }


}
