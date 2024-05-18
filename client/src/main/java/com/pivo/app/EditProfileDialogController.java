package com.pivo.app;

import static com.pivo.app.App.selectedUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import at.favre.lib.crypto.bcrypt.BCrypt;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class EditProfileDialogController {
    @FXML
    private TextField newUsername;
    @FXML
    private TextField newEmail;
    @FXML
    private PasswordField newPassword;

    private ProfileInformationController parentController;

    public void setParentController(ProfileInformationController controller) {
        this.parentController = controller;
    }

    @FXML
    private void handleUpdate() {
        String sql = "UPDATE users SET username = ?, email = ?, password_hash = ? WHERE username = ?";
        try (
                Connection conn = DatabaseController.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newUsername.getText());
            pstmt.setString(2, newEmail.getText());
            pstmt.setString(3, hashPassword(newPassword.getText()));
            pstmt.setString(4, selectedUser);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                showAlert("Success", "Profile updated successfully.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", "Failed to update profile.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
