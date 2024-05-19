package com.github.kxrxh.javalin.rest.api.jwt;

import lombok.Getter;

import java.util.UUID;

@Getter
public record UserPayload(UUID userId, String email) {
}
