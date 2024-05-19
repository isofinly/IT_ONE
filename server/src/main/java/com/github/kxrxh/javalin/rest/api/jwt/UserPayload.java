package com.github.kxrxh.javalin.rest.api.jwt;

import java.util.UUID;

public record UserPayload(UUID userId, String email) {
}
