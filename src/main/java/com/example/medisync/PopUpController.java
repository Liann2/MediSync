package com.example.medisync;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PopUpController implements Initializable {

    // JDBC URL, username, and password of MySQL server
    private static final String URL = "jdbc:mysql://localhost:3306/medisync";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    @FXML
    private TableView<Patient> PatientTableView;

    @FXML
    private TableColumn<Patient, Integer> patientIDColumn;

    @FXML
    private TableColumn<Patient, String> fNameColumn;

    @FXML
    private TableColumn<Patient, Integer> ageColumn;

    @FXML
    private TableColumn<Patient, LocalDate> birthDateColumn;

    @FXML
    private TableColumn<Patient, String> bloodTypeColumn;

    @FXML
    private TableColumn<Patient, String> sexColumn;

    @FXML
    private TableColumn<Patient, String> familyHistoryColumn;

    @FXML
    private TableColumn<Patient, String> homeAddressColumn;

    @FXML
    private TableColumn<Patient, String> phoneNumberColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupPatientTableView();
        fetchPatientsFromDatabase();
    }

    public void setupPatientTableView() {
        patientIDColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        fNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        birthDateColumn.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        bloodTypeColumn.setCellValueFactory(new PropertyValueFactory<>("bloodType"));
        sexColumn.setCellValueFactory(new PropertyValueFactory<>("sex"));
        familyHistoryColumn.setCellValueFactory(new PropertyValueFactory<>("familyHistory"));
        homeAddressColumn.setCellValueFactory(new PropertyValueFactory<>("homeAddress"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
    }

    // Function to fetch patients from database and populate table
    public void fetchPatientsFromDatabase() {
        Task<ObservableList<Patient>> task = new Task<>() {
            @Override
            protected ObservableList<Patient> call() throws Exception {
                ObservableList<Patient> patients = FXCollections.observableArrayList();

                try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                     PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM patients");
                     ResultSet rs = pstmt.executeQuery()) {

                    while (rs.next()) {
                        int patientId = rs.getInt("patient_id");
                        String fullName = rs.getString("full_name");
                        int age = rs.getInt("age");
                        LocalDate birthDate = rs.getDate("birth_date").toLocalDate();
                        String bloodType = rs.getString("blood_type");
                        String sex = rs.getString("sex");
                        String familyHistory = rs.getString("family_history");
                        String homeAddress = rs.getString("home_address");
                        String phoneNumber = rs.getString("phone_number");

                        Patient patient = new Patient(patientId, fullName, age, birthDate, bloodType, sex, familyHistory, homeAddress, phoneNumber);
                        patients.add(patient);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(PopUpController.class.getName()).log(Level.SEVERE, null, ex);
                }

                return patients;
            }
        };

        task.setOnSucceeded(e -> PatientTableView.setItems(task.getValue()));
        task.setOnFailed(e -> {
            Throwable exception = task.getException();
            exception.printStackTrace();
        });

        new Thread(task).start();
    }
}
