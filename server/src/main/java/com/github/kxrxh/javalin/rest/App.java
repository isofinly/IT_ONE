package com.github.kxrxh.javalin.rest;

import com.github.kxrxh.javalin.rest.api.RestServer;
import com.github.kxrxh.javalin.rest.database.DatabaseManager;
import com.github.kxrxh.javalin.rest.util.NATSSubscriber;

import java.io.IOException;

// TODO: Fix env file not loading properly
public class App {
    public static final NATSSubscriber NATS_SUBSCRIBER;

    static {
        try {
            NATS_SUBSCRIBER = new NATSSubscriber("nats://localhost:4222", "finance_updates");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        // Initialize the server
        RestServer server = new RestServer();
        server.setupRoutes();

        // Load JWT secret from environment variable
        String jwtSecret = System.getenv("JWT_SECRET");
        if (jwtSecret == null) {
            jwtSecret = "secret";
        }

        // Initialize protected routes
        server.setupJWTAuthentication("api/v1", jwtSecret);

        // Load database url from environment variable
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl == null) {
            databaseUrl = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:5432/postgres";
        }

        // Load database user from environment variable
        String databaseUser = System.getenv("DATABASE_USER");
        if (databaseUser == null) {
            databaseUser = "postgres.hrbitlxmswwrmhuqqjxt";
        }

        // Load database password from environment variable
        String databasePassword = System.getenv("DATABASE_PASSWORD");
        if (databasePassword == null) {
            databasePassword = "j7XGFhZQotjsk2vv";
        }

        // Initialize database manager with provided parameters
        DatabaseManager.initialize("org.postgresql.Driver",
                databaseUrl, databaseUser, databasePassword);

        // Load port from environment variable
        String strPort = System.getenv("PORT");
        if (strPort == null) {
            strPort = "3030";
        }
        int port = Integer.parseInt(strPort);

        // Start the server
        server.listen(port);
    }
}
