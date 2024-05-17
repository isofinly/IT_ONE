package com.pivo.app;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ConfigManager {
    ConfigManager() {
    }

    static JSONObject config;

    static {
        loadConfig();
    }

    private static void loadConfig() {
        try (FileInputStream inputStream = new FileInputStream("config.json")) {
            JSONTokener tokener = new JSONTokener(inputStream);
            config = new JSONObject(tokener);
        } catch (IOException e) {
            System.err.println("Error loading config: " + e.getMessage());
            config = new JSONObject();
        }
    }

    public static String getConfig(String key) {
        return config.optString(key);
    }

    public static void setConfig(String key, String value) {
        config.put(key, value);
        saveConfig();
    }

    private static void saveConfig() {
        try (FileOutputStream outputStream = new FileOutputStream("config.json")) {
            outputStream.write(config.toString(4).getBytes());
        } catch (IOException e) {
            System.err.println("Error saving config: " + e.getMessage());
        }
    }
}
