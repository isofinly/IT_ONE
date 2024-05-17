package com.pivo.app;

import at.favre.lib.crypto.bcrypt.BCrypt;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.pivo.app.Application.showAlert;

public class ProfileInformationController {
    private static final String ERROR = "Error";
    @FXML
    private Label usernameLabel, emailLabel, createdAtLabel;
    @FXML
    private TextField newUsername, newEmail;
    private String password_hash;
    @FXML
    private PasswordField newPassword;
    @FXML
    private PasswordField passwordField;

    @FXML
    public void initialize() {
        fetchUserInfo(); // Call this method when the view initializes
    }

    private void fetchUserInfo() {
        String userName = ConfigManager.getConfig("selectedUser");
        String sql = "SELECT username, email, created_at, password_hash FROM users WHERE username = ?";
        try (Connection conn = DatabaseController.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
            e.printStackTrace();
        }
    }

    @FXML
    private void createUser() {
        String sql = "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseController.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newUsername.getText());
            pstmt.setString(2, newEmail.getText());
            pstmt.setString(3, hashPassword(newPassword.getText())); // Hash the password before storing
            pstmt.executeUpdate();
            fetchUserInfo(); // Refresh user info display
            showAlert("Success", "User created successfully", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            showAlert(ERROR, "Failed to create new user", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    @FXML
    private boolean verifyPassword() {
        return BCrypt.verifyer().verify(passwordField.getText().toCharArray(), password_hash).verified;
    }

    public void editProfile() {
        if (verifyPassword()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("pages/settings/EditProfileDialog.fxml"));
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(loader.load()));
                EditProfileDialogController controller = loader.getController();
                controller.setParentController(this);
                stage.showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showAlert(ERROR, "Incorrect password", Alert.AlertType.ERROR);
        }
    }

    public String getCurrentUsername() {
        return usernameLabel.getText();
    }

}
