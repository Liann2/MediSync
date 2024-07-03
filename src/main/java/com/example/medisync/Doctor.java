package com.example.medisync;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Doctor {
    private StringProperty name;
    private StringProperty specialization;

    public Doctor(String name, String specialization) {
        this.name = new SimpleStringProperty(name);
        this.specialization = new SimpleStringProperty(specialization);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getSpecialization() {
        return specialization.get();
    }

    public StringProperty specializationProperty() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization.set(specialization);
    }
}
