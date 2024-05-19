package com.github.kxrxh.javalin.rest.util;

public class UninitializedSSL extends RuntimeException {

    public UninitializedSSL(String message) {
        super(message);
    }
}
