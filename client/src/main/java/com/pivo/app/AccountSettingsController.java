package com.pivo.app;

import static com.pivo.app.App.showAlert;
import static com.pivo.app.ConfigManager.getConfig;
import static com.pivo.app.ConfigManager.setConfig;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;

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
        App.showAlert("Success", "User changed successfully" + "\n" + "Please restart the app",
                Alert.AlertType.INFORMATION);
    }

    @FXML
    private void onViewpointChange() {
        String selectedViewpoint = viewpointSelector.getValue();
        setConfig("viewPoint", selectedViewpoint);
        App.showAlert("Success", "This is not implemented yet", Alert.AlertType.WARNING);
    }

    private void populateUsers() {
        try (Connection conn = DatabaseController.connect();
                Statement stmt = conn.createStatement();
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
