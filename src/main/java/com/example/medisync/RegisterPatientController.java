package com.example.medisync;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;


public class RegisterPatientController implements Initializable {

    @FXML
    private TextField fullNameField, patientIdField, ageField, homeAddressField, phoneNoField;

    @FXML
    private ComboBox<String> bloodTypeField;

    @FXML
    private CheckBox anemiaCb, asthmaCb, cancerCb,
            diabetesCb, heartDiseaseCb, hypertensionCb,
            strokeCb, liverDiseaseCb, kidneyDiseaseCb;

    @FXML
    private RadioButton maleButton, femaleButton;

    @FXML
    private DatePicker birthdateField;


    private Stage stage;
    private Scene scene;
    private Parent root;

    private String sex;


    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<String> list = FXCollections.observableArrayList("A", "B", "O", "AB");
        bloodTypeField.setItems(list);
    }


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

    public void switchToPatientQueue(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("PatientQueue.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void registerPatientInformation(ActionEvent event) {
        // Retrieve data from fields

        int patientId = Integer.parseInt(patientIdField.getText());
        String fullName = fullNameField.getText();
        int age = Integer.parseInt(ageField.getText());
        LocalDate birthdate = birthdateField.getValue();
        String bloodType = bloodTypeField.getSelectionModel().getSelectedItem();
        String homeAddress = homeAddressField.getText();
        String phoneNo = phoneNoField.getText();

        if(maleButton.isSelected()){
            sex = "M";
        }
        else if(femaleButton.isSelected()){
            sex = "F";
        }

// Retrieve selected checkboxes and build the family history string
        StringBuilder familyHistoryBuilder = new StringBuilder();

        if (diabetesCb.isSelected()) {
            familyHistoryBuilder.append("Diabetes, ");
        }
        if (asthmaCb.isSelected()) {
            familyHistoryBuilder.append("Asthma, ");
        }
        if (cancerCb.isSelected()) {
            familyHistoryBuilder.append("Cancer, ");
        }
        if (heartDiseaseCb.isSelected()) {
            familyHistoryBuilder.append("Heart Disease, ");
        }
        if (anemiaCb.isSelected()) {
            familyHistoryBuilder.append("Anemia, ");
        }
        if (hypertensionCb.isSelected()) {
            familyHistoryBuilder.append("Hypertension, ");
        }
        if (strokeCb.isSelected()) {
            familyHistoryBuilder.append("Stroke, ");
        }
        if (liverDiseaseCb.isSelected()) {
            familyHistoryBuilder.append("Liver Disease, ");
        }
        if (kidneyDiseaseCb.isSelected()) {
            familyHistoryBuilder.append("Kidney Disease, ");
        }

        // Remove the trailing comma and space if the string is not empty
        String familyHistory = familyHistoryBuilder.toString();
        if (familyHistory.endsWith(", ")) {
            familyHistory = familyHistory.substring(0, familyHistory.length() - 2);
        }



        // Display retrieved data (for testing)
        System.out.println("Patient ID: " + patientId);
        System.out.println("Full Name: " + fullName);
        System.out.println("Age: " + age);
        System.out.println("Birth Date: " + birthdate);
        System.out.println("Blood Type: " + bloodType);
        System.out.println("Sex: " + sex);
        System.out.println("Family History: " + familyHistory);
        System.out.println("Home Address: " + homeAddress);
        System.out.println("Phone Number: " + phoneNo);




        //PUT THESE INTO THE DATABASE
        insertPatientIntoDatabase(patientId, fullName, age, birthdate, bloodType, sex, familyHistory, homeAddress, phoneNo);
    }

    private void insertPatientIntoDatabase(int patientId, String fullName, int age, LocalDate birthdate, String bloodType,
                                           String sex, String familyHistory, String homeAddress, String phoneNo) {
        String insertQuery = "INSERT INTO patients (patient_id, full_name, age, birth_date, blood_type, sex, family_history, home_address, phone_number) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        final String URL = "jdbc:mysql://localhost:3306/medisync";
        final String USER = "root";
        final String PASSWORD = "";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            stmt.setInt(1, patientId);
            stmt.setString(2, fullName);
            stmt.setInt(3, age);
            stmt.setDate(4, java.sql.Date.valueOf(birthdate));
            stmt.setString(5, bloodType);
            stmt.setString(6, sex);
            stmt.setString(7, familyHistory);
            stmt.setString(8, homeAddress);
            stmt.setString(9, phoneNo);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                showSuccessAlert("Success", "Patient Inserted", "Patient data successfully inserted into the database.");
            } else {
                showErrorAlert("Error", "Insert Failed", "Failed to insert patient data into the database.");
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                showErrorAlert("Error", "Duplicate Entry", "A patient with this ID already exists in the database.");
            }
        }



    }

    private void showSuccessAlert(String title, String header, String content) {
        Alert successAlert = new Alert(Alert.AlertType.CONFIRMATION);
        successAlert.setTitle(title);
        successAlert.setHeaderText(header);
        successAlert.setContentText(content);
        successAlert.showAndWait();
    }

    private void showErrorAlert(String title, String header, String content) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle(title);
        errorAlert.setHeaderText(header);
        errorAlert.setContentText(content);
        errorAlert.showAndWait();
    }

    public void clearFields (ActionEvent event){
        fullNameField.clear();
        patientIdField.clear();
        ageField.clear();

    }
}

