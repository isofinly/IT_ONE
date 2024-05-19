package com.github.kxrxh.javalin.rest.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.github.kxrxh.javalin.rest.api.jwt.UserPayload;

class UserPayloadTest {

    @Test
    void testUserPayload() {
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";

        UserPayload userPayload = new UserPayload(userId, email);

        assertEquals(userId, userPayload.getUserId());
        assertEquals(email, userPayload.getEmail());
    }
}
