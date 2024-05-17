module com.pivo.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;

    requires atlantafx.base;
    requires java.sql;
    requires org.json;
    requires bcrypt;
    requires com.zaxxer.hikari;
    requires static lombok;
    requires io.nats.jnats;


    exports com.pivo.app;
    exports com.pivo.app.controllers;
    opens com.pivo.app to javafx.fxml;
    opens com.pivo.app.controllers to javafx.fxml;
    exports com.pivo.app.util;
    opens com.pivo.app.util to javafx.fxml;
    exports com.pivo.app.entities;
    opens com.pivo.app.entities to javafx.fxml;
}