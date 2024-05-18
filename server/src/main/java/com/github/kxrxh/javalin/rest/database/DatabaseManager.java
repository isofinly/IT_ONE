package com.github.kxrxh.javalin.rest.database;

import java.sql.Connection;
import java.util.Optional;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatabaseManager {
    private static DatabaseManager instance = null;
    private final HikariDataSource dataSource;

    // Private constructor to prevent instantiation from outside
    private DatabaseManager(String driver, String url, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(driver);
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);

        // Configure HikariCP settings as needed
        config.addDataSourceProperty("cachePrepStmts", "false");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setAutoCommit(true);


        dataSource = new HikariDataSource(config);
    }

    // Method to get the singleton instance of DatabaseManager
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("DatabaseManager is not initialized");
        }
        return instance;
    }

    // Method to initialize the database connection
    public static void initialize(String driver, String url, String username, String password) {
        if (instance == null) {
            instance = new DatabaseManager(driver, url, username, password);
        }
    }

    // Method to close the connection to the database
    public void disconnect() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    // Method to get the connection
    public Optional<Connection> getConnection() {
        if (dataSource == null) {
            return Optional.empty();
        }
        try {
            Connection connection = dataSource.getConnection();
            return Optional.of(connection);
        } catch (Exception e) {
            log.error("Retrieving connection failed: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
