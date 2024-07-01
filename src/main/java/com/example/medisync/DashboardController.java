package com.example.medisync;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

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




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populatePieChart();
        updateTotalPatientCount(); // Initialize total patient count on dashboard load
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
        Task<ObservableList<PieChart.Data>> task = new Task<ObservableList<PieChart.Data>>() {
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

        new Thread(task).start(); // Start the background task
    }



    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
