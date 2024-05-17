package com.pivo.app.controllers;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.pivo.app.Application;
import com.pivo.app.util.ConfigManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import static com.pivo.app.Application.publisher;
import static com.pivo.app.Application.showAlert;
import static com.pivo.app.util.ConfigManager.getConfig;
import static com.pivo.app.util.ConfigManager.setConfig;

@Slf4j
public class AccountSettingsController {
    private static final String ERROR = "Error";
    @FXML
    private TextField newUsername;
    @FXML
    private TextField newEmail;
    @FXML
    private PasswordField newPassword;
    @FXML
    private ComboBox<String> viewpointSelector;
    @FXML
    private TextField loginUsernameOrEmail;
    @FXML
    private PasswordField loginPassword;

    @FXML
    public void initialize() {
        populateViewpoints();
        viewpointSelector.setValue(getConfig("viewPoint"));
    }

    @FXML
    private void onViewpointChange() {
        String selectedViewpoint = viewpointSelector.getValue();
        setConfig("viewPoint", selectedViewpoint);
        Application.showAlert("Success", "This is not implemented yet", Alert.AlertType.WARNING);
    }

    @FXML
    private void loginUser() {
        String input = loginUsernameOrEmail.getText();
        String password = loginPassword.getText();

        String sql = "SELECT password_hash FROM users WHERE username = ? OR email = ?";
        try (Connection conn = DatabaseManager.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, input);
            pstmt.setString(2, input);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                if (BCrypt.verifyer().verify(password.toCharArray(), storedHash).verified) {
                    showAlert("Success", "Login successful", Alert.AlertType.INFORMATION);
                    // TODO: Safety concerns
                    ConfigManager.setConfig("selectedUser", input);
                } else {
                    showAlert(ERROR, "Invalid username/email or password", Alert.AlertType.ERROR);
                }
            } else {
                showAlert(ERROR, "Invalid username/email or password", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            showAlert(ERROR, "Failed to login", Alert.AlertType.ERROR);
            log.error("Failed to login user", e);
        }
    }

    @FXML
    private void createUser() {
        String sql = "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newUsername.getText());
            pstmt.setString(2, newEmail.getText());
            pstmt.setString(3, hashPassword(newPassword.getText())); // Hash the password before storing
            pstmt.executeUpdate();

            publisher.publishToNATS(sql, Arrays.asList(newUsername.getText(), newEmail.getText(), hashPassword(newPassword.getText())));
            showAlert("Success", "User created successfully", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            showAlert(ERROR, "Failed to create new user", Alert.AlertType.ERROR);
            log.error("Failed to create new user", e);
        }
    }

    private String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    private void populateViewpoints() {
        viewpointSelector.getItems().addAll("Standard View", "Fiscal Year View", "Custom View #1");
    }

}
