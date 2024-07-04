package com.example.medisync;

import javafx.beans.property.*;
import java.time.LocalDate; // Import LocalDate from java.time

public class Patient {
    private IntegerProperty patientId;
    private StringProperty fullName;
    private IntegerProperty age;
    private ObjectProperty<LocalDate> birthDate;
    private StringProperty bloodType;
    private StringProperty sex;
    private StringProperty familyHistory;
    private StringProperty homeAddress;
    private StringProperty phoneNumber;

    public Patient(int patientId, String fullName, int age, LocalDate birthDate, String bloodType,
                   String sex, String familyHistory, String homeAddress, String phoneNumber) {
        this.patientId = new SimpleIntegerProperty(patientId);
        this.fullName = new SimpleStringProperty(fullName);
        this.age = new SimpleIntegerProperty(age);
        this.birthDate = new SimpleObjectProperty<>(birthDate); // Correct usage of LocalDate here
        this.bloodType = new SimpleStringProperty(bloodType);
        this.sex = new SimpleStringProperty(sex);
        this.familyHistory = new SimpleStringProperty(familyHistory);
        this.homeAddress = new SimpleStringProperty(homeAddress);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
    }

    // Getters and setters for all properties
    public int getPatientId() {
        return patientId.get();
    }

    public IntegerProperty patientIdProperty() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId.set(patientId);
    }

    public String getFullName() {
        return fullName.get();
    }

    public StringProperty fullNameProperty() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName.set(fullName);
    }

    public int getAge() {
        return age.get();
    }

    public IntegerProperty ageProperty() {
        return age;
    }

    public void setAge(int age) {
        this.age.set(age);
    }

    public LocalDate getBirthDate() {
        return birthDate.get();
    }

    public ObjectProperty<LocalDate> birthDateProperty() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate.set(birthDate);
    }

    public String getBloodType() {
        return bloodType.get();
    }

    public StringProperty bloodTypeProperty() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType.set(bloodType);
    }

    public String getSex() {
        return sex.get();
    }

    public StringProperty sexProperty() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex.set(sex);
    }

    public String getFamilyHistory() {
        return familyHistory.get();
    }

    public StringProperty familyHistoryProperty() {
        return familyHistory;
    }

    public void setFamilyHistory(String familyHistory) {
        this.familyHistory.set(familyHistory);
    }

    public String getHomeAddress() {
        return homeAddress.get();
    }

    public StringProperty homeAddressProperty() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress.set(homeAddress);
    }

    public  String getPhoneNumber() {
        return phoneNumber.get();
    }

    public StringProperty phoneNumberProperty() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.set(phoneNumber);
    }
}
