module com.pivo.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires atlantafx.base;
    requires java.sql;
    requires jdk.jfr;
    requires org.json;

    opens com.pivo.app to javafx.fxml;
    exports com.pivo.app;
}