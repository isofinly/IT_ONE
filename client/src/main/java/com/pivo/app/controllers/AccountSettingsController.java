package com.pivo.app.controllers;

import com.pivo.app.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.pivo.app.Application.showAlert;
import static com.pivo.app.util.ConfigManager.getConfig;
import static com.pivo.app.util.ConfigManager.setConfig;

public class AccountSettingsController {

    @FXML
    private ComboBox<String> userSelector;
    @FXML
    private ComboBox<String> viewpointSelector;

    @FXML
    public void initialize() {
        populateUsers();
        populateViewpoints();
        userSelector.setValue(getConfig("selectedUser"));
        viewpointSelector.setValue(getConfig("viewPoint"));
    }

    @FXML
    private void onUserChange() {
        String selectedUser = userSelector.getValue();
        setConfig("selectedUser", selectedUser);
        Application.showAlert("Success", "User changed successfully" + "\n" + "Please restart the app", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void onViewpointChange() {
        String selectedViewpoint = viewpointSelector.getValue();
        setConfig("viewPoint", selectedViewpoint);
        Application.showAlert("Success", "This is not implemented yet", Alert.AlertType.WARNING);
    }

    private void populateUsers() {
        try (Connection conn = DatabaseManager.connect(); Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT username FROM users")) {
            while (rs.next()) {
                userSelector.getItems().add(rs.getString("username"));
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to fetch users", Alert.AlertType.ERROR);
            throw new RuntimeException(e);
        }
    }

    private void populateViewpoints() {
        viewpointSelector.getItems().addAll("Standard View", "Fiscal Year View", "Custom View #1");
    }

}
