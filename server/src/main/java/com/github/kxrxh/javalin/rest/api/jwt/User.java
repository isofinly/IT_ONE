package com.github.kxrxh.javalin.rest.api.jwt;

public class User {
    public final Long userId;
    public final String username;

    User(Long userId, String username) {
        this.userId = userId;
        this.username = username;
    }
}
