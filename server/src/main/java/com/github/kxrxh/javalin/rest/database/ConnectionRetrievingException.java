package com.github.kxrxh.javalin.rest.database;

public class ConnectionRetrievingException extends RuntimeException {
    public ConnectionRetrievingException(String message) {
        super(message);
    }

    public ConnectionRetrievingException() {
        this("Could not get database connection");
    }
}
