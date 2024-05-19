package com.pivo.app.controllers;

import static com.pivo.app.App.showAlert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.pivo.app.App;
import com.pivo.app.util.ConfigManager;

import at.favre.lib.crypto.bcrypt.BCrypt;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProfileInformationController {
    private static final String ERROR = "Error";
    @FXML
    private Label usernameLabel, emailLabel, createdAtLabel;
    @FXML
    private String password_hash;
    @FXML
    private PasswordField passwordField;

    @FXML
    public void initialize() {
        fetchUserInfo(); // Call this method when the view initializes
    }

    private void fetchUserInfo() {
        String userName = ConfigManager.getConfig("selectedUser");
        String sql = "SELECT username, email, created_at, password_hash FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                password_hash = rs.getString("password_hash");
                usernameLabel.setText(rs.getString("username"));
                emailLabel.setText(rs.getString("email"));
                createdAtLabel.setText(rs.getString("created_at"));
            }
        } catch (SQLException e) {
            showAlert(ERROR, "Failed to fetch user information", Alert.AlertType.ERROR);
            log.error("Failed to fetch user information", e);
        }
    }

    public void editProfile() {
        if (verifyPassword()) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        App.class.getResource("pages/settings/EditProfileDialog.fxml"));
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(loader.load()));
                EditProfileDialogController controller = loader.getController();
                controller.setParentController(this);
                stage.showAndWait();
            } catch (Exception e) {
                log.error("Failed to load edit profile dialog", e);
            }
        } else {
            showAlert(ERROR, "Incorrect password", Alert.AlertType.ERROR);
        }
    }

    private boolean verifyPassword() {
        return BCrypt.verifyer().verify(passwordField.getText().toCharArray(), password_hash).verified;
    }

}
