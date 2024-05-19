package com.github.kxrxh.javalin.rest.util;

import com.github.kxrxh.javalin.rest.database.ConnectionRetrievingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConnectionRetrievingExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String errorMessage = "Custom error message";
        ConnectionRetrievingException exception = new ConnectionRetrievingException(errorMessage);
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void testConstructorWithoutMessage() {
        ConnectionRetrievingException exception = new ConnectionRetrievingException();
        assertEquals("Could not get database connection", exception.getMessage());
    }
}
