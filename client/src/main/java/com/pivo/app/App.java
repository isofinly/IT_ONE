package com.pivo.app;

import static com.pivo.app.util.ConfigManager.getConfig;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import com.pivo.app.controllers.DatabaseManager;
import com.pivo.app.util.NATSPublisher;
import com.pivo.app.util.ThemeUtil;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App extends Application {
    public static final String selectedUser = getConfig("selectedUser");
    public static final NATSPublisher publisher;

    static {
        try {
            publisher = new NATSPublisher("nats://localhost:4222");
        } catch (IOException | InterruptedException e) {
            log.error("Failed to connect to NATS server", e);
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

    public static int fetchUserId() throws SQLException {
        String query = "SELECT user_id FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, selectedUser);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                } else {
                    log.error("User not found");
                    throw new SQLException("User not found");
                }
            }
        }
    }

    // TODO Add a splash screen
    @Override
    public void start(Stage primaryStage) throws Exception {
        String theme = getConfig("appearance");
        ThemeUtil.changeTheme(Objects.requireNonNullElse(theme, "PrimerDark"));
        Parent root = FXMLLoader.load(Objects.requireNonNull(App.class.getResource("pages/Application.fxml")));
        Scene scene = new Scene(root);
        try {
            primaryStage.getIcons()
                    .add(new Image(Objects.requireNonNull(App.class.getResourceAsStream("logo.png"))));
        } catch (NullPointerException e) {
            log.error("No logo found", e);
        }

        primaryStage.setTitle("IT_ONE Личные Финансы");
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            try {
                NATSPublisher.close();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Platform.exit();
            System.exit(0);
        });
    }
}
