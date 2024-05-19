package com.pivo.app;

import atlantafx.base.theme.*;
import javafx.application.Application;

public class ThemeUtil {
    ThemeUtil() {
    }

    public static void changeTheme(String themeName) {
        switch (themeName) {
            case "CupertinoDark":
                Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());
                break;
            case "CupertinoLight":
                Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());
                break;
            case "Dracula":
                Application.setUserAgentStylesheet(new Dracula().getUserAgentStylesheet());
                break;
            case "NordDark":
                Application.setUserAgentStylesheet(new NordDark().getUserAgentStylesheet());
                break;
            case "NordLight":
                Application.setUserAgentStylesheet(new NordLight().getUserAgentStylesheet());
                break;
            case "PrimerDark":
                Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
                break;
            case "PrimerLight":
                Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
                break;
            default:
                System.err.println("Theme not supported: " + themeName);
                break;
        }
        ConfigManager.setConfig("appearance", themeName);
    }
}
