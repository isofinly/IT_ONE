package com.github.kxrxh.javalin.rest.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UninitializedSSLTest {

    @Test
    void testConstructor() {
        String message = "SSL is not initialized";
        UninitializedSSL uninitializedSSL = new UninitializedSSL(message);
        assertEquals(message, uninitializedSSL.getMessage());
    }
}