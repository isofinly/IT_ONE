package com.github.kxrxh.javalin.rest.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrometheusInitializationExceptionTest {

    @Test
    void testConstructor() {
        Exception cause = new Exception("Sample cause");
        PrometheusInitializationException exception = new PrometheusInitializationException(cause);
        assertEquals("Failed to initialize Prometheus", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
