package com.github.kxrxh.javalin.rest.util;

import com.github.kxrxh.javalin.rest.api.jwt.NotInitialized;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NotInitializedTest {

    @Test
    void testConstructor() {
        NotInitialized exception = new NotInitialized();
        assertEquals("AuthHandlers not initialized. Please call 'initialize' method first.", exception.getMessage());
    }
}
