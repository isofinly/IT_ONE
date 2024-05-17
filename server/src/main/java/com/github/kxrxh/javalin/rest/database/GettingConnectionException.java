package com.github.kxrxh.javalin.rest.database;

public class GettingConnectionException extends RuntimeException {
    public GettingConnectionException(String message) {
        super(message);
    }

    public GettingConnectionException() {
        this("Could not get database connection");
    }
}
