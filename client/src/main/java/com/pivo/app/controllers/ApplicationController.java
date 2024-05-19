package com.pivo.app.controllers;

import java.io.IOException;
import java.util.Objects;

import com.pivo.app.App;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApplicationController {

    @FXML
    private TabPane mainTabPane;

    @FXML
    public void initialize() {
        loadTab("pages/tabs/DashboardTab.fxml", "Dashboard");
        loadTab("pages/tabs/TransactionsTab.fxml", "Transactions");
        loadTab("pages/tabs/ValuationsTab.fxml", "Valuations");
        loadTab("pages/tabs/SettingsTab.fxml", "Settings");
    }

    private void loadTab(String resource, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(resource));
            Tab tab = new Tab(title);
            tab.setContent(loader.load());
            mainTabPane.getTabs().add(tab);
        } catch (IOException e) {
            loadFallbackTab(title, e);
        }
    }

    private void loadFallbackTab(String title, Exception e) {
        log.error("Failed to load tab '{}': {}", title, e.getMessage());

        try {
            Node errorContent = FXMLLoader.load(Objects.requireNonNull(App.class.getResource("pages/ErrorPage.fxml")));
            Tab errorTab = new Tab(title, errorContent);
            mainTabPane.getTabs().add(errorTab);
        } catch (IOException ex) {
            log.error("Failed to load the fallback error page: {}", ex.getMessage());
            // As a last resort, just display a static error message
            Tab errorTab = new Tab(title);
            errorTab.setContent(new Label("Critical error: Cannot load the interface."));
            mainTabPane.getTabs().add(errorTab);
        }
    }

}
