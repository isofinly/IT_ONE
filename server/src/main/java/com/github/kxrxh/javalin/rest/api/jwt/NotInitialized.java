package com.github.kxrxh.javalin.rest.api.jwt;

public class NotInitialized extends RuntimeException {
    public NotInitialized() {
        super("AuthHandlers not initialized. Please call 'initialize' method first.");
    }
}
