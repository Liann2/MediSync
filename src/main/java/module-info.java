module com.example.medisync {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;
    requires net.synedra.validatorfx;
    requires eu.hansolo.tilesfx;
    requires java.desktop;
    requires java.management;


    opens com.example.medisync to javafx.fxml;
    exports com.example.medisync;
}