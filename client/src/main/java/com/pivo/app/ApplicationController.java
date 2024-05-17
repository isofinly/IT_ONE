package com.pivo.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.IOException;

public class ApplicationController {

    @FXML
    private TabPane mainTabPane;

    @FXML
    public void initialize() {
        loadTab("pages/DashboardTab.fxml", "Dashboard");
        loadTab("pages/TransactionsTab.fxml", "Transactions");
        loadTab("pages/ValuationsTab.fxml", "Valuations");
        loadTab("pages/SettingsTab.fxml", "Settings");
    }

    private void loadTab(String resource, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            Tab tab = new Tab(title);
            tab.setContent(loader.load());
            mainTabPane.getTabs().add(tab);
        } catch (IOException e) {
            loadFallbackTab(title, e);
        }
    }
    private void loadFallbackTab(String title, Exception e) {
        // TODO handle exception
        System.err.println("Failed to load tab '" + title + "': " + e.getMessage());
        try {
            Node errorContent = FXMLLoader.load(getClass().getResource("pages/ErrorPage.fxml"));
            Tab errorTab = new Tab(title, errorContent);
            mainTabPane.getTabs().add(errorTab);
        } catch (IOException ex) {
            // TODO handle exception
            System.err.println("Failed to load the fallback error page: " + ex.getMessage());
            // As a last resort, just display a static error message
            Tab errorTab = new Tab(title);
            errorTab.setContent(new Label("Critical error: Cannot load the interface."));
            mainTabPane.getTabs().add(errorTab);
        }
    }

}
