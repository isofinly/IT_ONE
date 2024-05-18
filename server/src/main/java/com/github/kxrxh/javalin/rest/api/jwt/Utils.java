package com.github.kxrxh.javalin.rest.api.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.kxrxh.javalin.rest.database.DatabaseManager;
import io.javalin.http.Context;
import javalinjwt.JavalinJWT;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
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

    public static List<UUID> getAllUserIds() {
        List<UUID> userIds = new ArrayList<>();
        String query = "SELECT user_id FROM users";

        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            log.error("Could not get connection from database manager");
            return userIds;
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                UUID userId = UUID.fromString(rs.getString("user_id"));
                userIds.add(userId);
            }
        } catch (SQLException e) {
            log.error("Error retrieving user IDs: {}", e.getMessage());
        }

        return userIds;
    }

}
