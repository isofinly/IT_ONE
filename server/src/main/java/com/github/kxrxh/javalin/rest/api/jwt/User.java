package com.github.kxrxh.javalin.rest.api.jwt;

public class User {
    public final Long id;
    public final String username;

    User(Long id, String username) {
        this.id = id;
        this.username = username;

    }
}
