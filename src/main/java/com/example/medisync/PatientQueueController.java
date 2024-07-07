package com.example.medisync;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.Timer;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class PatientQueueController implements Initializable {
    private static final String URL = "jdbc:mysql://localhost:3306/medisync";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    //DOCTOR TABLE AND COLUMN
    @FXML
    private TableView<Doctor> doctorQueueTable;
    @FXML
    private TableColumn<Doctor, StringProperty> doctorNameColumn;
    @FXML
    private TableColumn<Doctor, StringProperty> specializationColumn;
    @FXML
    private TableColumn<Doctor, StringProperty> availabilityColumn;
    @FXML
    private TableColumn<Doctor, StringProperty> remainingTimeColumn;

    private ObservableList<Doctor> doctorList = FXCollections.observableArrayList();


    //Hey chat, use these as referrence. They'll be called from the Database.
    @FXML
    private TableView<Appointment> patientQueueTable;
    @FXML
    private TableColumn<Appointment, LocalDate> appointmentDateColumn;
    @FXML
    private TableColumn<Appointment, LocalTime> bookedTimeColumn;
    @FXML
    private TableColumn<Appointment, StringProperty> patientNameColumn;
    @FXML
    private TableColumn<Appointment, StringProperty> pSpecializationColumn;
    @FXML
    private TableColumn<Appointment, Void> actionColumn;
    private ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();



    private Stage stage;
    private Scene scene;
    private Parent root;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        doctorNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        specializationColumn.setCellValueFactory(new PropertyValueFactory<>("specialization"));
        availabilityColumn.setCellValueFactory(new PropertyValueFactory<>("availability"));
        remainingTimeColumn.setCellValueFactory(new PropertyValueFactory<>("remainingTime"));

        loadDoctorData();


        appointmentDateColumn.setCellValueFactory(new PropertyValueFactory<>("appointment_date"));
        bookedTimeColumn.setCellValueFactory(new PropertyValueFactory<>("appointment_time"));
        patientNameColumn.setCellValueFactory(new PropertyValueFactory<>("full_name"));
        pSpecializationColumn.setCellValueFactory(new PropertyValueFactory<>("pref_specialization"));

        addButtonToTable();

        loadAppointmentData();

        bookedTimeColumn.setCellValueFactory(new PropertyValueFactory<>("appointment_time"));

// Handle null values in the cell factory
        bookedTimeColumn.setCellFactory(column -> new TableCell<Appointment, LocalTime>() {
            @Override
            protected void updateItem(LocalTime item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
            }
        });


    }

    private void loadDoctorData() {
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            String query = "SELECT name, specialization FROM doctors";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String specialization = resultSet.getString("specialization");
                String availability = "Available"; // Placeholder for availability
                String remainingTime = "10 mins"; // Placeholder for remaining time

                doctorList.add(new Doctor(name, specialization, availability, remainingTime));
            }

            doctorQueueTable.setItems(doctorList);
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void addButtonToTable() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Appoint");

            {
                btn.setOnAction(event -> {
                    Appointment appointment = getTableView().getItems().get(getIndex());
                    System.out.println("Appoint clicked for: " + appointment.getFull_name());
                    assignPatientToDoctor(appointment);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
    }

    private void loadAppointmentData() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/medisync", "root", "");
            String query = "SELECT * FROM appointments";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int appointment_id = resultSet.getInt("appointment_id");
                int patient_id = resultSet.getInt("patient_id");
                String full_name = resultSet.getString("full_name");
                LocalDate appointment_date = resultSet.getDate("appointment_date").toLocalDate();
                LocalTime appointment_time = resultSet.getTime("appointment_time").toLocalTime();
                String pref_specialization = resultSet.getString("pref_specialization");

                appointmentList.add(new Appointment(appointment_id, patient_id, full_name, appointment_date, appointment_time, pref_specialization));
            }

            patientQueueTable.setItems(appointmentList);
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void assignPatientToDoctor(Appointment appointment) {
        for (Doctor doctor : doctorList) {
            if (doctor.getSpecialization().equals(appointment.getPref_specialization()) && doctor.getAvailability().equals("Available")) {
                // Assign patient to doctor
                doctor.setAvailability("Occupied");
                doctor.setRemainingTime("15 mins"); // Example remaining time

                // Print or log assignment for debugging purposes
                System.out.println("Assigned patient " + appointment.getFull_name() + " to doctor " + doctor.getName());

                // Remove from UI immediately
                appointmentList.remove(appointment);

                // Schedule removal from database based on appointment time
                scheduleAppointmentRemoval(appointment, doctor);

                break;
            }
        }
    }


    private void scheduleAppointmentRemoval(Appointment appointment, Doctor doctor) {
        long delayInSeconds = 15; // Fixed delay of 15 seconds

        Timer timer = new Timer();

        AtomicInteger remainingSeconds = new AtomicInteger((int) delayInSeconds);

        // Initial update of remaining time
        doctor.setRemainingTime(formatRemainingTime(remainingSeconds.get()));

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    // Update remaining time in Doctor object
                    doctor.setRemainingTime(formatRemainingTime(remainingSeconds.get()));
                    remainingSeconds.getAndDecrement();

                    if (remainingSeconds.get() < 0) {
                        // Remove from database
                        removeAppointmentFromDatabase(appointment);

                        // Cancel the timer task
                        timer.cancel();
                    }
                });
            }
        }, 0, 1000); // Schedule to update every second
    }

    private String formatRemainingTime(int remainingSeconds) {
        if (remainingSeconds <= 0) {
            return "00:00";
        } else {
            int minutes = remainingSeconds / 60;
            int seconds = remainingSeconds % 60;
            return String.format("%02d:%02d", minutes, seconds); // Format as "MM:SS"
        }
    }



    private long calculateSecondsUntilAppointment(LocalDate appointmentDate, LocalTime appointmentTime) {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        long secondsUntilAppointment = 0;

        if (currentDate.isBefore(appointmentDate) || (currentDate.equals(appointmentDate) && currentTime.isBefore(appointmentTime))) {
            // Calculate time difference in seconds
            long currentSeconds = currentDate.toEpochDay() * 86400 + currentTime.toSecondOfDay();
            long appointmentSeconds = appointmentDate.toEpochDay() * 86400 + appointmentTime.toSecondOfDay();

            secondsUntilAppointment = appointmentSeconds - currentSeconds;
        }

        return secondsUntilAppointment;
    }



    private void removeAppointmentFromDatabase(Appointment appointment) {
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            String query = "DELETE FROM appointments WHERE appointment_id = " + appointment.getAppointment_id();
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void switchToRegisterPatient(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("RegisterPatient.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
