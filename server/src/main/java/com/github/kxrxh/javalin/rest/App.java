package com.github.kxrxh.javalin.rest;

import com.github.kxrxh.javalin.rest.api.RestServer;
import com.github.kxrxh.javalin.rest.database.DatabaseManager;
import com.github.kxrxh.javalin.rest.util.NATSSubscriber;
import com.github.kxrxh.javalin.rest.util.NATSUtil;
import io.javalin.util.JavalinBindException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j(topic = "App")
public class App {
    public static void main(String[] args) {
        String natsUrl = System.getenv("NATS_URL");
        if (natsUrl == null) {
            log.warn("NATS_URL environment variable not set");
            natsUrl = "nats://localhost:4222";
        }

        try {
            NATSUtil.connect(natsUrl);
            NATSSubscriber.subscribe("client_updates");
        } catch (IOException e) {
            log.error("Unable to connect to NATS: {}", e.getMessage());
            Thread.currentThread().interrupt();
            System.exit(1);
        }

        // Initialize the server
        RestServer server;
        // If "DEV" environment variable is set -> use dev mode
        String isDev = System.getenv("DEV");
        if (isDev == null) {
            server = new RestServer();
        } else {
            log.warn("Using DEVELOPER MODE");
            server = new RestServer(true);
        }

        server.setupRoutes();

        // Load JWT secret from environment variable
        String jwtSecret = System.getenv("JWT_SECRET");
        if (jwtSecret == null) {
            log.warn("JWT_SECRET environment variable not set");
            System.exit(1);
        }

        // Initialize protected routes
        server.setupJWTAuthentication("api/v1", jwtSecret);

        // Load database url from environment variable
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl == null) {
            log.warn("DATABASE_URL environment variable not set");
            System.exit(1);
        }

        // Load database user from environment variable
        String databaseUser = System.getenv("DATABASE_USER");
        if (databaseUser == null) {
            log.warn("DATABASE_USER environment variable not set");
            System.exit(1);
        }

        // Load database password from environment variable
        String databasePassword = System.getenv("DATABASE_PASSWORD");
        if (databasePassword == null) {
            log.warn("DATABASE_PASSWORD environment variable not set");
            System.exit(1);
        }

        // Initialize database manager with provided parameters
        DatabaseManager.initialize("org.postgresql.Driver",
                databaseUrl, databaseUser, databasePassword);

        // Load port from environment variable
        String strPort = System.getenv("PORT");
        if (strPort == null) {
            log.warn("PORT environment variable not set, using default port");
            strPort = "3030";
        }
        int port = Integer.parseInt(strPort);

        // Start the server
        try {
            server.listen(port);
        } catch (JavalinBindException e) {
            log.error("Unable to bind to port {}: {}", port, e.getMessage());
            System.exit(1);
        }
    }
}
