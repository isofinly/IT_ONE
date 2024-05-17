package com.github.kxrxh.javalin.rest.database;

import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.DatabaseConfig;

import java.util.Properties;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Database database;

    private DatabaseManager(String driver, String url, String username, String password) {
        Properties properties = new Properties();
        properties.put("ebean.db.ddl.generate", "true");
        properties.put("ebean.platform", "sqlite");
        properties.put("ebean.db.logStatements", "true");
        properties.put("evolutionplugin", "disabled");
        
        properties.put("ebean.default", "com.github.kxrxh.javalin.rest.database.models.*");

        properties.put("datasource.db.username", username);
        properties.put("datasource.db.password", password);
        properties.put("datasource.db.databaseUrl", url);
        properties.put("datasource.db.databaseDriver", driver);

        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.loadFromProperties(properties);
        dbConfig.name("default");

        this.database = DatabaseFactory.create(dbConfig);

    }

    public static void initialize(String driver, String url, String username, String password) {
        instance = new DatabaseManager(driver, url, username, password);
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("DatabaseManager not initialized");
        }
        return instance;
    }

    public Database getDatabase() {
        return database;
    }
}