package com.github.kxrxh.javalin.rest.api.jwt;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.github.kxrxh.javalin.rest.database.DatabaseUserManager;
import com.github.kxrxh.javalin.rest.database.models.Users;
import io.javalin.http.Context;
import javalinjwt.JWTGenerator;
import javalinjwt.JWTProvider;
import javalinjwt.JavalinJWT;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Handles authentication-related operations such as token generation and
 * validation.
 */
@Slf4j
public class AuthHandlers {

    private static AuthHandlers instance;
    private final JWTProvider<User> provider;

    private AuthHandlers(Algorithm algorithm, JWTVerifier verifier) {
        JWTGenerator<User> generator = (user, alg) -> {
            JWTCreator.Builder token = JWT.create();
            try {
                Field[] fields = User.class.getDeclaredFields();
                for (Field field : fields) {
                    Object value = field.get(user);
                    if (value != null) {
                        token.withClaim(field.getName(), value.toString());
                    }
                }
            } catch (IllegalAccessException e) {
                log.error("Error generating token: {}", e.getMessage());
            }
            return token.sign(alg);
        };

        this.provider = new JWTProvider<>(algorithm, generator, verifier);
    }

    /**
     * Creates a new instance of AuthHandlers with the specified secret key.
     *
     * @param secret The secret key used for token generation and validation.
     */
    public static void initialize(String secret) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm).build();
        instance = new AuthHandlers(algorithm, verifier);
    }

    /**
     * Handles security for routes requiring authentication.
     *
     * @param context The Javalin context object.
     * @throws RuntimeException if AuthHandlers is not initialized.
     */
    public static void securityRouteHandler(Context context) {
        if (instance == null) {
            throw new NotInitialized();
        }

        // Allow login and register routes
        // May be isn't the best way to do this, but it works for now.
        if (context.path().endsWith("/login") || context.path().endsWith("/register")) {
            return;
        }

        Optional<DecodedJWT> decodedJWT = JavalinJWT.getTokenFromHeader(context)
                .flatMap(instance.provider::validateToken);
        if (decodedJWT.isEmpty()) {
            context.status(401).result("Unauthorized");
        }
    }

    /**
     * Handles login route.
     *
     * @param context The Javalin context object.
     */
    public static void loginRouteHandler(Context context) {
        ObjectReader reader = new ObjectMapper().readerFor(AuthRequest.class);

        AuthRequest result;
        try {
            result = reader.readValue(context.body());
        } catch (JsonProcessingException e) {
            context.status(400).result("Bad request. Unable to decode json body!");
            return;
        }

        // Searching for user by username in database
        Optional<Users> user;
        try {
            user = DatabaseUserManager.getUser(result.getUsername());
        } catch (SQLException e) {
            context.status(500).result("Internal server error. Unable to fetch user from database!");
            return;
        }
        if (user.isEmpty()) {
            context.status(401).result("Wrong username or password!");
            return;
        }

        // Verifying password
        BCrypt.Result bcryptResult = BCrypt.verifyer().verify(result.getPassword().toCharArray(),
                user.get().getPassword());
        if (bcryptResult.verified) {
            // Generate token
            User userObj = new User(user.get().getUserId(), user.get().getUsername());
            String token = instance.provider.generateToken(userObj);

            // Return token to the client in JSON format
            Map<String, String> map = new HashMap<>();
            map.put("token", token);
            context.json(map);
            return;
        }

        context.status(401).result("Wrong username or password!");
    }

    /**
     * Handles register route.
     *
     * @param context The Javalin context object.
     */
    public static void registerRouteHandler(Context context) {
        ObjectReader reader = new ObjectMapper().readerFor(AuthRequest.class);

        AuthRequest result;
        try {
            result = reader.readValue(context.body());
        } catch (JsonProcessingException e) {
            context.status(400).result("Bad request. Unable to decode json body!");
            return;
        }

        // Searching for user by username in database
        Optional<Users> user;
        try {
            user = DatabaseUserManager.getUser(result.getUsername());
        } catch (SQLException e) {
            context.status(500).result("Internal server error. Unable to fetch user from database!");
            return;
        }
        if (user.isPresent()) {
            context.status(401).result("Username already exists!");
            return;
        }

        String hashedPassword = BCrypt.withDefaults().hashToString(12, result.getPassword().toCharArray());
        Optional<Users> newUser;
        try {
            newUser = DatabaseUserManager.createUser(result.getUsername(), hashedPassword);
            if (newUser.isEmpty()) {
                context.status(500).result("Internal server error. Unable to create user in database!");
            } else {
                context.status(200).result("User created successfully!");
            }
        } catch (SQLException e) {
            context.status(500).result("Internal server error. Unable to create user in database!");
        }
    }
}
