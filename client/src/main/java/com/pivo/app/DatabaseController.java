package com.pivo.app;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseController {
    // Private constructor to prevent instantiation
    private DatabaseController() {
    }

    public static Connection connect() throws SQLException, IOException {
        FileInputStream inputStream = new FileInputStream("config.json");
        JSONObject jsonObject = new JSONObject(new JSONTokener(inputStream));
        String databaseFilePath = jsonObject.getString("databasePath");
        File file = new File(databaseFilePath);

        if (!file.exists()) {
            System.err.println("Database file does not exist. Creating a new one.");
            boolean created = new File(databaseFilePath).createNewFile();
            if (!created) {
                System.err.println("Failed to create the database file. Could not continue.");
                System.exit(1);
            }
            DatabaseController.createDatabase(databaseFilePath);
        }

        // TODO Add check that schema is correct
        return DriverManager.getConnection("jdbc:sqlite:" + databaseFilePath);
    }

    private static void createDatabase(String databaseFilePath) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + databaseFilePath);
             Statement stmt = conn.createStatement();
             BufferedReader reader = new BufferedReader(new InputStreamReader(Application.class.getResourceAsStream("schema.sql")))) {
            String line;
            StringBuilder sql = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sql.append(line).append("\n");
                if (line.trim().endsWith(";")) {
                    stmt.execute(sql.toString());
                    sql = new StringBuilder();
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read the schema file.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Failed to create the SQLite database.");
            e.printStackTrace();
        }
    }
}
