package com.pivo.app.controllers;

import com.pivo.app.util.AutoCommitTextFieldTableCell;
import com.pivo.app.util.ConfigManager;
import com.pivo.app.util.Pair;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.json.JSONObject;

import static com.pivo.app.Application.showAlert;

public class ConfigurationController {

    @FXML
    private TableView<Pair<String, String>> configTable;
    @FXML
    private TableColumn<Pair<String, String>, String> keyColumn;
    @FXML
    private TableColumn<Pair<String, String>, String> valueColumn;

    @FXML
    public void initialize() {
        loadConfigurations();

        keyColumn.setCellValueFactory(param -> param.getValue().keyProperty());
        valueColumn.setCellValueFactory(param -> param.getValue().valueProperty());

        valueColumn.setCellFactory(column -> new AutoCommitTextFieldTableCell<>());
        valueColumn.setOnEditCommit(
                event -> configTable.getItems().get(event.getTablePosition().getRow())
                        .setValue(event.getNewValue()));

        configTable.setEditable(true);
    }

    private void loadConfigurations() {
        JSONObject config = ConfigManager.config;
        config.keySet().forEach(key ->
                configTable.getItems().add(new Pair<>(key, config.getString(key)))
        );
    }

    @FXML
    private void saveConfig() {
        configTable.getItems().forEach(pair ->
                ConfigManager.setConfig(pair.getKey(), pair.getValue())
        );
        showAlert("Success", "Configuration saved successfully", Alert.AlertType.INFORMATION);
    }
}
