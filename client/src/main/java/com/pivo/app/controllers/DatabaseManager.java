package com.pivo.app.controllers;

import com.pivo.app.Application;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public class DatabaseManager {

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
                log.error("Database file does not exist. Creating a new one.");
                boolean created = new File(databaseFilePath).createNewFile();
                if (!created) {
                    log.error("Failed to create the database file. Could not continue.");
                    System.exit(1);
                }
                createDatabase(databaseFilePath);
            }

            config.setJdbcUrl("jdbc:sqlite:" + databaseFilePath);
            dataSource = new HikariDataSource(config);

        } catch (IOException e) {
            log.error("Error loading database configuration", e);
            throw new RuntimeException("Error loading database configuration", e);
        }
    }

    // Private constructor to prevent instantiation
    private DatabaseManager() {
    }

    public static Connection connect() throws SQLException {
        return dataSource.getConnection();
    }

    private static void createDatabase(String databaseFilePath) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + databaseFilePath); Statement stmt = conn.createStatement(); BufferedReader reader = new BufferedReader(new InputStreamReader(Application.class.getResourceAsStream("schema.sql")))) {
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
            log.error("Failed to read the schema file.", e);
        } catch (SQLException e) {
            log.error("Failed to create the SQLite database.", e);
        }
    }
}
