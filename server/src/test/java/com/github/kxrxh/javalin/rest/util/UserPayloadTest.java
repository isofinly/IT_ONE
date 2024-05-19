package com.github.kxrxh.javalin.rest.util;

import com.github.kxrxh.javalin.rest.api.jwt.UserPayload;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserPayloadTest {

    @Test
    void testUserPayload() {
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";

        UserPayload userPayload = new UserPayload(userId, email);

        assertEquals(userId, userPayload.userId());
        assertEquals(email, userPayload.email());
    }
}
