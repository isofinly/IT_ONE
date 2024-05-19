package com.github.kxrxh.javalin.rest.util;

public class PrometheusInitializationException extends RuntimeException {
    public PrometheusInitializationException(Exception e) {
        super("Failed to initialize Prometheus", e);
    }
}
