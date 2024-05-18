package com.github.kxrxh.javalin.rest.api.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserPayload {
    public final UUID userId;
    public final String email;
}
