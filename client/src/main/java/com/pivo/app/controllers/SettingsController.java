package com.pivo.app.controllers;

import java.io.IOException;
import java.util.Objects;

import com.pivo.app.App;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SettingsController {
    @FXML
    private ListView<String> configList;

    @FXML
    private ListView<String> settingsList;
    @FXML
    private StackPane detailsPane;

    @FXML
    public void initialize() {
        settingsList.getItems().addAll("Profile Information", "Appearances", "Account settings", "Configuration");
        settingsList.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> loadSetting(newValue));
    }

    private void loadSetting(String setting) {
        try {
            Node node = switch (setting) {
                case "Account settings" ->
                    FXMLLoader.load(Objects
                            .requireNonNull(App.class.getResource("pages/settings/AccountSettings.fxml")));
                case "Appearances" ->
                    FXMLLoader.load(
                            Objects.requireNonNull(App.class.getResource("pages/settings/Appearance.fxml")));
                case "Configuration" ->
                    FXMLLoader.load(
                            Objects.requireNonNull(App.class.getResource("pages/settings/Configuration.fxml")));
                case "Profile Information" ->
                    FXMLLoader.load(Objects
                            .requireNonNull(App.class.getResource("pages/settings/ProfileInformation.fxml")));
                default -> new Label("Selection does not have a corresponding view.");
            };
            detailsPane.getChildren().setAll(node);
        } catch (IOException e) {
            log.error("Failed to load setting: {}", setting, e);
        }
    }
}
