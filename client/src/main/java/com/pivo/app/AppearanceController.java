package com.pivo.app;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class AppearanceController {

    @FXML
    private ListView<String> themeList;

    @FXML
    public void initialize() {
        themeList.getItems().addAll(
                "CupertinoDark", "CupertinoLight", "Dracula", "NordDark", "NordLight", "PrimerDark", "PrimerLight");
        themeList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                ThemeUtil.changeTheme(newValue);
            }
        });
    }
}
