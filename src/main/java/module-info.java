module com.miage.fleet {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.miage.fleet to javafx.graphics, javafx.fxml;
    opens com.miage.fleet.controller to javafx.fxml;
    opens com.miage.fleet.util to javafx.fxml;

    exports com.miage.fleet.model;
    exports com.miage.fleet.service;
}
