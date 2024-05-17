package com.pivo.app;

import atlantafx.base.theme.PrimerDark;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApplication extends Application {
    public static void main(String[] args) {
        // TODO implement logging
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        Parent root = FXMLLoader.load(MainApplication.class.getResource("pages/Application.fxml"));
        Scene scene = new Scene(root);
        try {
            primaryStage.getIcons().add(new Image(MainApplication.class.getResourceAsStream("logo.png")));
        } catch (NullPointerException e) {
            // TODO handle exception
            System.err.println("No logo found");
        }

        primaryStage.setTitle("IT_ONE Личные Финансы");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
