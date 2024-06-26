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
    public void loginUser(ActionEvent event) throws IOException{

            receptionEmail = emailField.getText();
            receptionPassword = passwordField.getText();

            if(emailField.getText().isEmpty()|| passwordField.getText().isEmpty()){
                loginButton.setDisable(true);

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Denied");
                alert.setHeaderText("Your login was denied.");
                alert.setContentText("Please put your credentials in the appropriate fields.");
                alert.showAndWait();
                loginButton.setDisable(false);
            }
            else if(!emailField.getText().equals("receptionABC") || !passwordField.getText().equals("receptionABC")){
                loginButton.setDisable(true);

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Denied");
                alert.setHeaderText("Invalid Credentials.");
                alert.setContentText("There is no record of the provided credentials.");
                alert.showAndWait();
                loginButton.setDisable(false);
            }
            else {
                root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
    }



}
