package com.github.kxrxh.javalin.rest.services;

import com.github.kxrxh.javalin.rest.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NotificationService {

    public static void setNotification(Long userId, String notificationType, Long threshold) throws SQLException {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "INSERT INTO notifications (user_id, notification_type, threshold) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setLong(1, userId);
            ps.setString(2, notificationType);
            ps.setLong(3, threshold);
            ps.executeUpdate();
        }
    }
}
