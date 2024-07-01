package com.example.medisync;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AppointmentSchedulerController {

    private static final String URL = "jdbc:mysql://localhost:3306/medisync";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    @FXML
    private TextField  patientIdField;
    @FXML
    private TextField patientNameField,patientAgeField, sexField,
            dateOfBirthField, bloodTypeField, homeAddressField,
            phoneField;
    @FXML
    private TextArea familyHealthHistoryField;




    private Stage stage;
    private Scene scene;
    private Parent root;
    private AnchorPane scenePane;



    public void logoutUser(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("You are about to Logout!");
        alert.setContentText("Are you sure you want to Logout?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            root = FXMLLoader.load(getClass().getResource("Login.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
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

    public void switchToPatientQueue(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("PatientQueue.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    public void clearFields (ActionEvent event){
        patientIdField.clear();
    }

//    public void searchPatient(ActionEvent event) {
//        String patientId = patientIdField.getText();
//        if (patientId.isEmpty()) {
//            showAlert("Error", "Please enter a Patient ID.");
//            return;
//        }
//
//        String query = "SELECT * FROM patients WHERE patient_id = ?";
//        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
//             PreparedStatement pstmt = conn.prepareStatement(query)) {
//
//            pstmt.setString(1, patientId);
//            try (ResultSet rs = pstmt.executeQuery()) {
//                if (rs.next()) {
//                    // Assuming your database columns are named accordingly
//                    patientNameField.setText(rs.getString("full_name"));
//                    patientAgeField.setText(rs.getString("age"));
//                    sexField.setText(rs.getString("sex"));
//                    dateOfBirthField.setText(rs.getString("birth_date"));
//                    bloodTypeField.setText(rs.getString("blood_type"));
//                    homeAddressField.setText(rs.getString("home_address"));
//                    phoneField.setText(rs.getString("phone_number"));
//                    familyHealthHistoryField.setText(rs.getString("family_history"));
//                } else {
//                    showAlert("Not Found", "No patient found with ID: " + patientId);
//                }
//            }
//        } catch (SQLException e) {
//            showAlert("Error", "Error fetching patient information: " + e.getMessage());
//        }
//    }

    public void searchPatient(ActionEvent event) {
        String patientId = patientIdField.getText();
        if (patientId.isEmpty()) {
            showAlert("Error", "Please enter a Patient ID.");
            return;
        }

        // Create a Task to perform the database query
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String query = "SELECT * FROM patients WHERE patient_id = ?";
                try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                     PreparedStatement pstmt = conn.prepareStatement(query)) {

                    pstmt.setString(1, patientId);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            String fullName = rs.getString("full_name");
                            String age = rs.getString("age");
                            String sex = rs.getString("sex");
                            String birthDate = rs.getString("birth_date");
                            String bloodType = rs.getString("blood_type");
                            String homeAddress = rs.getString("home_address");
                            String phoneNumber = rs.getString("phone_number");
                            String familyHistory = rs.getString("family_history");

                            // Update UI on the JavaFX Application Thread
                            Platform.runLater(() -> {
                                patientNameField.setText(fullName);
                                patientAgeField.setText(age);
                                sexField.setText(sex);
                                dateOfBirthField.setText(birthDate);
                                bloodTypeField.setText(bloodType);
                                homeAddressField.setText(homeAddress);
                                phoneField.setText(phoneNumber);
                                familyHealthHistoryField.setText(familyHistory);
                            });
                        } else {
                            Platform.runLater(() -> showAlert("Not Found", "No patient found with ID: " + patientId));
                        }
                    }
                } catch (SQLException e) {
                    Platform.runLater(() -> showAlert("Error", "Error fetching patient information: " + e.getMessage()));
                }
                return null;
            }
        };

        // Start the task in a new thread (background thread)
        Thread thread = new Thread(task);
        thread.start();
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

