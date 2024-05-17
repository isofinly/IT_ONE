package com.pivo.app;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

import static com.pivo.app.ConfigManager.getConfig;

public class Application extends javafx.application.Application {
    static String selectedUser = getConfig("selectedUser");
    static String userName = getConfig("selectedUser");
    static Connection conn;

    static {
        try {
            conn = DatabaseController.connect();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void showAlert(String title, String message, Alert.AlertType alertType) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    // TODO Add a splash screen and logging
    @Override
    public void start(Stage primaryStage) throws Exception {
        String theme = ConfigManager.getConfig("appearance");
        ThemeUtil.changeTheme(Objects.requireNonNullElse(theme, "PrimerDark"));
        Parent root = FXMLLoader.load(Application.class.getResource("pages/Application.fxml"));
        Scene scene = new Scene(root);
        try {
            primaryStage.getIcons().add(new Image(Application.class.getResourceAsStream("logo.png")));
        } catch (NullPointerException e) {
            // TODO handle exception
            System.err.println("No logo found");
        }

        primaryStage.setTitle("IT_ONE Личные Финансы");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


}
