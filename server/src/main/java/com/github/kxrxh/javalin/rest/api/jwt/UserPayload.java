package com.github.kxrxh.javalin.rest.api.jwt;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserPayload {
    public final UUID userId;
    public final String username;
}
