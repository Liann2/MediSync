package com.example.medisync;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {
    private int appointment_id;
    private int patient_id;
    private String full_name;
    private LocalDate appointment_date;
    private LocalTime appointment_time;
    private StringProperty pref_specialization;

    public Appointment(int appointment_id, int patient_id, String full_name, LocalDate appointment_date, LocalTime appointment_time, String pref_specialization) {
        this.appointment_id = appointment_id;
        this.patient_id = patient_id;
        this.full_name = full_name;
        this.appointment_date = appointment_date;
        this.appointment_time = appointment_time;
        this.pref_specialization = new SimpleStringProperty(pref_specialization);
    }

    public int getAppointment_id() {
        return appointment_id;
    }

    public void setAppointment_id(int appointment_id) {
        this.appointment_id = appointment_id;
    }

    public int getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(int patient_id) {
        this.patient_id = patient_id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public LocalDate getAppointment_date() {
        return appointment_date;
    }

    public void setAppointment_date(LocalDate appointment_date) {
        this.appointment_date = appointment_date;
    }

    public LocalTime getAppointment_time() {
        return appointment_time;
    }

    public void setAppointment_time(LocalTime appointment_time) {
        this.appointment_time = appointment_time;
    }

    public String getPref_specialization() {
        return pref_specialization.get();
    }

    public StringProperty pref_specializationProperty() {
        return pref_specialization;
    }

    public void setPref_specialization(String pref_specialization) {
        this.pref_specialization.set(pref_specialization);
    }

    public StringProperty appointmentTimeProperty() {
        return new SimpleStringProperty(appointment_time.toString());
    }

    public StringProperty fullNameProperty() {
        return new SimpleStringProperty(full_name);
    }
}
