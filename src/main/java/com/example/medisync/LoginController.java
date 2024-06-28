package com.example.medisync;


import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class LoginController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    String receptionEmail, receptionPassword;

    private static final String URL = "jdbc:mysql://localhost:3306/medisync";
    private static final String USER = "root";
    private static final String PASSWORD = "";


    public void loginUser(ActionEvent event) throws IOException {
        String enteredEmail = emailField.getText();
        String enteredPassword = passwordField.getText();

        if (enteredEmail.isEmpty() || enteredPassword.isEmpty()) {
            showLoginError("Your login was denied.", "Please fill in both email and password fields.");
            return;
        }

        // Check credentials against database
        boolean authenticated = checkCredentials(enteredEmail, enteredPassword);

        if (authenticated) {
            root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else {
            showLoginError("Invalid Credentials.", "The email or password is incorrect.");
        }
    }

    private boolean checkCredentials(String email, String password) {
        String query = "SELECT * FROM reception WHERE email = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Returns true if a matching record is found
            }

        } catch (SQLException e) {
            System.err.println("Error checking credentials: " + e.getMessage());
            return false;
        }
    }

    private void showLoginError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Denied");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
