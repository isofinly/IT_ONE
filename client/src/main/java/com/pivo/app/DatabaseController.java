package com.pivo.app;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseController {

    private static final HikariDataSource dataSource;

    static {
        // TODO On db update refresh values
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.sqlite.JDBC");
        config.addDataSourceProperty("cachePrepStmts", "false");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        try (FileInputStream inputStream = new FileInputStream("config.json")) {
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
                createDatabase(databaseFilePath);
            }

            config.setJdbcUrl("jdbc:sqlite:" + databaseFilePath);
            dataSource = new HikariDataSource(config);

        } catch (IOException | SQLException e) {
            throw new RuntimeException("Error loading database configuration", e);
        }
    }

    // Private constructor to prevent instantiation
    private DatabaseController() {
    }

    public static Connection connect() throws SQLException {
        return dataSource.getConnection();
    }

//    public static Connection connect() throws SQLException {
//        FileInputStream inputStream = null;
//        try {
//            inputStream = new FileInputStream("config.json");
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//        JSONObject jsonObject = new JSONObject(new JSONTokener(inputStream));
//        String databaseFilePath = jsonObject.getString("databasePath");
//        File file = new File(databaseFilePath);
//
//        if (!file.exists()) {
//            System.err.println("Database file does not exist. Creating a new one.");
//            boolean created = false;
//            try {
//                created = new File(databaseFilePath).createNewFile();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            if (!created) {
//                System.err.println("Failed to create the database file. Could not continue.");
//                System.exit(1);
//            }
//            DatabaseController.createDatabase(databaseFilePath);
//        }
//
//        // TODO Add check that schema is correct
//        return DriverManager.getConnection("jdbc:sqlite:" + databaseFilePath);
//    }


    private static void createDatabase(String databaseFilePath) throws SQLException {
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
