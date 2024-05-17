package com.github.kxrxh.javalin.rest.api;

import com.github.kxrxh.javalin.rest.api.jwt.AuthHandlers;
import io.javalin.Javalin;

/**
 * Represents a RESTful server built with Javalin framework.
 */
public class RestServer {
    private final Javalin app;

    /**
     * Constructs a new RestServer.
     */
    public RestServer() {
        this.app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.bundledPlugins.enableCors(cors -> cors.addRule(it -> it.allowHost("localhost")));
        });
    }

    /**
     * Sets up open routes for the RESTful API.
     */
    public void setupRoutes() {
        app.get("/", ctx -> ctx.result("Hello World!"));
    }

    /**
     * Sets up JWT authentication for protected routes.
     * 
     * @param protectedRoute The route prefix for protected routes.
     * @param jwtSecret      The secret key used for JWT token generation and
     *                       validation.
     */
    public void setupJWTAuthentication(String protectedRoute, String jwtSecret) {
        AuthHandlers.initialize("my-key");
        app.before(protectedRoute + "/*", AuthHandlers::securityRouteHandler);

        app.post(protectedRoute + "/login", AuthHandlers::loginRouteHandler);
        app.post(protectedRoute + "/register", AuthHandlers::registerRouteHandler);
    }

    /**
     * Starts the server and listens on the specified port.
     * 
     * @param port The port number on which the server will listen.
     */
    public void listen(int port) {
        app.start(port);
    }
}
