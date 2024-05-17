package com.github.kxrxh.javalin.rest.api.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class User {
    public final Long userId;
    public final String username;
}
