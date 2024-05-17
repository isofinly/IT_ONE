package com.github.kxrxh.javalin.rest.api.jwt;

public class UserPayload {
    public final Long userId;
    public final String username;

    UserPayload(Long userId, String username) {
        this.userId = userId;
        this.username = username;
    }
}
