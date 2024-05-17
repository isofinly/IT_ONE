package com.pivo.app;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.sql.Connection;

public class MainApplication extends Application {

    public static Connection conn;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        Parent root = FXMLLoader.load(MainApplication.class.getResource("dashboard.fxml"));
        Scene scene = new Scene(root);
        try {
            primaryStage.getIcons().add(new Image(MainApplication.class.getResourceAsStream("logo.png")));
        } catch (NullPointerException e) {
            System.err.println("Icon not found");
        }

        primaryStage.setTitle("IT_ONE Личные Финансы");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
