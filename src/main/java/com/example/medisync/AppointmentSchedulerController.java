package com.example.medisync;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class AppointmentSchedulerController implements Initializable {

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


    //APPOINT SCHEDULE
    @FXML
    private ComboBox<String> setTimeField;
    @FXML
    private DatePicker appointmentDateField;
    @FXML
    private ComboBox<String> appointSpecializationDropdown;


    //FOR SCHEDULING
    private int confirmedId;
    private String patientName;


    private Stage stage;
    private Scene scene;
    private Parent root;
    private AnchorPane scenePane;

    public void initialize(URL url, ResourceBundle rb){
        ObservableList<String> specializations = FXCollections.observableArrayList(
                "Orthopedist", "Dermatologist", "Pediatrician", "OB-GYN",
                "Cardiologist", "Gastroenterologist", "Psychiatrist", "Family Medicine");
        appointSpecializationDropdown.setItems(specializations);

        ObservableList<String> timeSlots = FXCollections.observableArrayList(
                "7:00 AM", "9:00 AM", "11:00 AM",
                "12:00 PM", "2:00 PM", "4:00 PM",
                "6:00 PM", "8:00 PM", "10:00 PM");
        setTimeField.setItems(timeSlots);

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
        patientNameField.clear();
        patientAgeField.clear();
        sexField.clear();
        dateOfBirthField.clear();
        bloodTypeField.clear();
        homeAddressField.clear();
        phoneField.clear();
        familyHealthHistoryField.clear();
    }


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

                                patientName = fullName;
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

    public void confirmPatient(ActionEvent event) {

        this.patientName = patientNameField.getText();
        this.confirmedId = Integer.parseInt(patientIdField.getText());
       showConfirm("Confirm Patient","Appointing the Patient","Are you sure that patient "+ confirmedId +" is making an appointment? ");
    }

    public void appointSchedule(ActionEvent event){

        int confirmedIdToDatabase = this.confirmedId;
        String patientNameToDatabase = this.patientName;
        LocalDate appointmentDate = appointmentDateField.getValue();

        String prefferedSpecialization = appointSpecializationDropdown.getValue();


        String appointmentTime = null;
//        "07:00", "09:00", "11:00",
//                "12:00", "14:00", "16:00",
//                "18:00", "20:00", "22:00"
        String selectedTime = setTimeField.getValue();
        LocalTime finalAppointmentTime = null;

        if (selectedTime != null) {
            switch (selectedTime) {
                case "7:00 AM":
                    finalAppointmentTime = LocalTime.parse("07:00");
                    break;
                case "9:00 AM":
                    finalAppointmentTime = LocalTime.parse("09:00");
                    break;
                case "11:00 AM":
                    finalAppointmentTime = LocalTime.parse("11:00");
                    break;
                case "12:00 PM":
                    finalAppointmentTime = LocalTime.parse("12:00");
                    break;
                case "2:00 PM":
                    finalAppointmentTime = LocalTime.parse("14:00");
                    break;
                case "4:00 PM":
                    finalAppointmentTime = LocalTime.parse("16:00");
                    break;
                case "6:00 PM":
                    finalAppointmentTime = LocalTime.parse("18:00");
                    break;
                case "8:00 PM":
                    finalAppointmentTime = LocalTime.parse("20:00");
                    break;
                case "10:00 PM":
                    finalAppointmentTime = LocalTime.parse("22:00");
                    break;
            }
        } else {
            showAlert("Missing Fields","Please Choose a time for appointment.");
        }




        System.out.println("Confirmed ID: " + confirmedIdToDatabase);
        System.out.println("Patient Name: " + patientNameToDatabase);
        System.out.println("Appointment Date: " + appointmentDate);
        System.out.println("Appointment Time: " + finalAppointmentTime);
        System.out.println("Preferred Specialization: " + prefferedSpecialization);

        insertAppointment(confirmedIdToDatabase,patientNameToDatabase,appointmentDate,finalAppointmentTime ,prefferedSpecialization);

    }

    public void insertAppointment(int patientId, String patientName, LocalDate appointmentDate, LocalTime appointmentTime, String prefSpecialization) {
        String insertQuery = "INSERT INTO appointments (patient_id, full_name, appointment_date, appointment_time, pref_specialization) " +
                "VALUES (?, ?, ?, ?, ?)";
        final String URL = "jdbc:mysql://localhost:3306/medisync";
        final String USER = "root";
        final String PASSWORD = "";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            stmt.setInt(1, patientId);
            stmt.setString(2, patientName);
            stmt.setDate(3, java.sql.Date.valueOf(appointmentDate));
            stmt.setTime(4, java.sql.Time.valueOf(appointmentTime));
            stmt.setString(5, prefSpecialization);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                showConfirm("Success", "Appointment Scheduled", "Appointment data successfully inserted into the database.");
            } else {
                showAlert("Error",  "Failed to insert appointment data into the database.");
            }

        } catch (SQLException e) {
            showAlert("Error",  "Error inserting appointment: " + e.getMessage());
        }
    }
    public void clearAppointmentFields(ActionEvent event) {
        // Clear appointment related fields
        appointSpecializationDropdown.getSelectionModel().clearSelection();
        appointmentDateField.setValue(null);
        // Clear radio buttons selection
        setTimeField.setValue(null);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showConfirm(String title,String header,String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

