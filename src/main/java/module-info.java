module com.example.medisync {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires net.synedra.validatorfx;
    requires eu.hansolo.tilesfx;

    opens com.example.medisync to javafx.fxml;
    exports com.example.medisync;
}