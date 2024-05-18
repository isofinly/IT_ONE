package com.github.kxrxh.javalin.rest.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import org.apache.commons.dbcp.BasicDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatabaseManager {
    private static DatabaseManager instance = null;
    private final BasicDataSource dataSource;

    // Private constructor to prevent instantiation from outside
    private DatabaseManager(String driver, String url, String username, String password) {
        dataSource = new BasicDataSource();

        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        dataSource.setMinIdle(5);
        dataSource.setMaxIdle(10);
        dataSource.setMaxOpenPreparedStatements(100);
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
    public void disconnect() throws SQLException {
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
