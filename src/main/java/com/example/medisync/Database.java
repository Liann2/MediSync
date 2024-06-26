package com.example.medisync;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/medisync";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to the database!");

            // Retrieve data from the reception table
            String receptionQuery = "SELECT email, password FROM reception";
            PreparedStatement receptionStatement = connection.prepareStatement(receptionQuery);
            ResultSet receptionResultSet = receptionStatement.executeQuery();
            while (receptionResultSet.next()) {
                String email = receptionResultSet.getString("email");
                String password = receptionResultSet.getString("password");
                System.out.println("Reception - Email: " + email + ", Password: " + password);
            }

            // Retrieve data from the patient table
            String patientQuery = "SELECT patient_id, full_name, birth_date, home_address, phone_number, sex, blood_type, family_history FROM patient";
            PreparedStatement patientStatement = connection.prepareStatement(patientQuery);
            ResultSet patientResultSet = patientStatement.executeQuery();
            while (patientResultSet.next()) {
                int patientId = patientResultSet.getInt("patient_id");
                String fullName = patientResultSet.getString("full_name");
                String birthDate = patientResultSet.getString("birth_date");
                String homeAddress = patientResultSet.getString("home_address");
                String phoneNumber = patientResultSet.getString("phone_number");
                String sex = patientResultSet.getString("sex");
                String bloodType = patientResultSet.getString("blood_type");
                String familyHistory = patientResultSet.getString("family_history");
                System.out.println("Patient - ID: " + patientId + ", Name: " + fullName + ", Birth Date: " + birthDate +
                        ", Address: " + homeAddress + ", Phone: " + phoneNumber + ", Sex: " + sex +
                        ", Blood Type: " + bloodType + ", Family History: " + familyHistory);
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
