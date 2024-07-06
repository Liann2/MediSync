package com.example.medisync;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.swing.*;
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

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;


public class DashboardController implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;
    private AnchorPane scenePane;

    private static final String URL = "jdbc:mysql://localhost:3306/medisync";
    private static final String USER = "root";
    private static final String PASSWORD = "";


    @FXML
    private PieChart totalPatientChart;

    @FXML
    private Label totalPatientCountLabel;

    @FXML
    private TableView<Doctor> doctorTableView;
    @FXML
    private TableColumn<Doctor, String> nameColumn;
    @FXML
    private TableColumn<Doctor, String> specializationColumn;
    @FXML
    private TableColumn<Doctor, Boolean> statusColumn;

    @FXML
    private TreeTableView<Appointment> appointmentTable;
    @FXML
    private TreeTableColumn<Appointment, String> appointmentTimeColumn;
    @FXML
    private TreeTableColumn<Appointment, String> appointmentNameColumn;

    @FXML
    private ImageView patientTableIcon;
//

    @FXML
    public void handlePopUp(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PatientTablePopUp.fxml"));
            Parent root1 = (Parent) loader.load();
            Stage stage1 = new Stage();
            stage1.setTitle("Patient Database");
            stage1.setScene(new Scene(root1));
            stage1.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR!");
            alert.setContentText("FUCKING ERROR?");



        }

    }




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populatePieChart();
        updateTotalPatientCount();
        setupDoctorTableView();
        populateDoctorTableView();
        setupAppointmentTableView();
        populateAppointmentTable();

        startThreadMonitor();
    }

    private void startThreadMonitor() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        Thread monitorThread = new Thread(() -> {
            while (true) {
                int threadCount = threadMXBean.getThreadCount();
                System.out.println("Current number of threads: " + threadCount);
                try {
                    Thread.sleep(5000); // Adjust the interval as needed
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        monitorThread.setDaemon(true); // Set the monitor thread as a daemon thread
        monitorThread.start();
    }

    private void setupDoctorTableView() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        specializationColumn.setCellValueFactory(new PropertyValueFactory<>("specialization"));

        statusColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Doctor, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Doctor, Boolean> param) {
                return new SimpleBooleanProperty(false);
            }
        });

        statusColumn.setCellFactory(col -> new TableCell<Doctor, Boolean>() {
            private final ToggleButton toggleButton = new ToggleButton("Available");
            private final Label statusLabel = new Label("Unavailable");

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(10, toggleButton, statusLabel));
                    toggleButton.setSelected(item != null && item);
                    toggleButton.setText(toggleButton.isSelected() ? "Toggle" : "Toggle");
                    statusLabel.setText(toggleButton.isSelected() ? "Available" : "Unavailable");

                    toggleButton.setOnAction(event -> {
                        boolean isSelected = toggleButton.isSelected();
                        toggleButton.setText(isSelected ? "Toggle" : "Toggle");
                        statusLabel.setText(isSelected ? "Available" : "Unavailable");
                    });
                }
            }
        });
    }

    private void populateDoctorTableView() {
        Task<ObservableList<Doctor>> task = new Task<>() {
            @Override
            protected ObservableList<Doctor> call() throws Exception {
                ObservableList<Doctor> doctors = FXCollections.observableArrayList();
                String query = "SELECT name, specialization FROM doctors";

                try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                     PreparedStatement pstmt = conn.prepareStatement(query);
                     ResultSet rs = pstmt.executeQuery()) {

                    while (rs.next()) {
                        String name = rs.getString("name");
                        String specialization = rs.getString("specialization");
                        doctors.add(new Doctor(name, specialization));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new Exception("Database query failed", e);
                }

                return doctors;
            }
        };

        task.setOnSucceeded(e -> doctorTableView.setItems(task.getValue()));
        task.setOnFailed(e -> showErrorAlert("Database Error", "Error fetching doctor data: " + task.getException().getMessage()));

        new Thread(task).start();
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

    public void populatePieChart() {
        Task<ObservableList<PieChart.Data>> task = new Task<>() {
            @Override
            protected ObservableList<PieChart.Data> call() throws Exception {
                ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
                String query = "SELECT sex, COUNT(*) as count FROM patients GROUP BY sex";

                try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                     PreparedStatement pstmt = conn.prepareStatement(query);
                     ResultSet rs = pstmt.executeQuery()) {

                    while (rs.next()) {
                        String sex = rs.getString("sex");
                        int count = rs.getInt("count");
                        pieChartData.add(new PieChart.Data(sex, count));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new Exception("Database query failed", e);
                }

                return pieChartData;
            }
        };

        task.setOnSucceeded(e -> totalPatientChart.setData(task.getValue()));
        task.setOnFailed(e -> showErrorAlert("Database Error", "Error fetching patient data: " + task.getException().getMessage()));

        new Thread(task).start();
    }

    public Task<Integer> fetchTotalPatientCountTask() {
        return new Task<>() {
            @Override
            protected Integer call() throws Exception {
                String query = "SELECT COUNT(*) AS totalPatients FROM patients";

                try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                     PreparedStatement pstmt = conn.prepareStatement(query);
                     ResultSet rs = pstmt.executeQuery()) {

                    if (rs.next()) {
                        return rs.getInt("totalPatients");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new Exception("Error fetching total patients", e);
                }

                return 0;
            }
        };
    }

    private void updateTotalPatientCount() {
        Task<Integer> task = fetchTotalPatientCountTask();
        task.setOnSucceeded(event -> {
            int totalPatients = task.getValue();
            totalPatientCountLabel.setText(String.valueOf(totalPatients));
        });

        task.setOnFailed(event -> showErrorAlert("Database Error", "Error fetching total patients: " + task.getException().getMessage()));

        new Thread(task).start();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void setupAppointmentTableView() {
        appointmentTimeColumn.setCellValueFactory(param -> param.getValue().getValue().appointmentTimeProperty());
        appointmentNameColumn.setCellValueFactory(param -> param.getValue().getValue().fullNameProperty());
    }


    private void populateAppointmentTable() {
        Task<ObservableList<Appointment>> task = new Task<>() {
            @Override
            protected ObservableList<Appointment> call() throws Exception {
                ObservableList<Appointment> appointments = FXCollections.observableArrayList();
                String query = "SELECT appointment_id, patient_id, full_name, appointment_date, appointment_time, pref_specialization FROM appointments";

                try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                     PreparedStatement pstmt = conn.prepareStatement(query);
                     ResultSet rs = pstmt.executeQuery()) {

                    while (rs.next()) {
                        int appointmentId = rs.getInt("appointment_id");
                        int patientId = rs.getInt("patient_id");
                        String fullName = rs.getString("full_name");
                        LocalDate appointmentDate = rs.getDate("appointment_date").toLocalDate();
                        LocalTime appointmentTime = null;
                        if (rs.getTime("appointment_time") != null) {
                            appointmentTime = rs.getTime("appointment_time").toLocalTime();
                        }
                        String prefSpecialization = rs.getString("pref_specialization");
                        appointments.add(new Appointment(appointmentId, patientId, fullName, appointmentDate, appointmentTime, prefSpecialization));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new Exception("Database query failed", e);
                }

                return appointments;
            }
        };

        task.setOnSucceeded(e -> {
            ObservableList<TreeItem<Appointment>> treeItems = FXCollections.observableArrayList();
            for (Appointment appointment : task.getValue()) {
                TreeItem<Appointment> item = new TreeItem<>(appointment);
                treeItems.add(item);
            }
            TreeItem<Appointment> rootItem = new TreeItem<>(new Appointment(0, 0, "", null, null, ""));
            rootItem.getChildren().setAll(treeItems);
            appointmentTable.setRoot(rootItem);
            appointmentTable.setShowRoot(false);
        });

        task.setOnFailed(e -> showErrorAlert("Database Error", "Error fetching appointment data: " + task.getException().getMessage()));

        new Thread(task).start();
    }

}