package com.github.kxrxh.javalin.rest.api.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.javalin.http.Context;
import javalinjwt.JavalinJWT;

import java.util.UUID;

public class Utils {
    private Utils() {
    }

    public static UUID getUUIDFromContext(Context context) {
        DecodedJWT decodedJWT = JavalinJWT.getDecodedFromContext(context);
        return decodedJWT.getClaim("userId").as(UUID.class);
    }

    public static String getUserEmailFromContext(Context context) {
        DecodedJWT decodedJWT = JavalinJWT.getDecodedFromContext(context);
        return decodedJWT.getClaim("email").asString();
    }
}