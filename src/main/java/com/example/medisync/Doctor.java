package com.example.medisync;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Doctor {
    private StringProperty name;
    private StringProperty specialization;
    private StringProperty availability;
    private StringProperty remainingTime;

    // Constructor for DashboardController
    public Doctor(String name, String specialization) {
        this.name = new SimpleStringProperty(name);
        this.specialization = new SimpleStringProperty(specialization);
    }

    // Constructor for PatientQueueController
    public Doctor(String name, String specialization, String availability, String remainingTime) {
        this.name = new SimpleStringProperty(name);
        this.specialization = new SimpleStringProperty(specialization);
        this.availability = new SimpleStringProperty(availability);
        this.remainingTime = new SimpleStringProperty("00:00");
    }



    // Getters and setters
    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }
    public void setName(String name) { this.name.set(name); }

    public String getSpecialization() { return specialization.get(); }

    public String getAvailability() { return availability.get(); }

    public void setAvailability(String availability) { this.availability.set(availability); }

    public void setRemainingTime(String remainingTime) { this.remainingTime.set(remainingTime); }
}