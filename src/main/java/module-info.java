module com.relief_system {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires javafx.web;

    opens com.relief_system to javafx.fxml;
    exports com.relief_system;

    opens com.model to javafx.fxml;
    exports com.model;

}
