package com.github.kxrxh.javalin.rest.services;

import com.github.kxrxh.javalin.rest.database.ConnectionRetrievingException;
import com.github.kxrxh.javalin.rest.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class NotificationService extends AbstractService {

    private NotificationService() {
    }

    /**
     * Sets a notification for a user.
     *
     * @param userId           The ID of the user for whom the notification is being
     *                         set.
     * @param notificationType The type of notification.
     * @param threshold        The threshold for the notification.
     * @throws SQLException If an SQL error occurs.
     */
    public static void setNotification(UUID userId, String notificationType, long threshold) throws SQLException {
        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();

        if (opConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = opConn.get();

        String query = "INSERT INTO notifications (user_id, notification_type, threshold) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setObject(1, userId, java.sql.Types.OTHER);
            ps.setString(2, notificationType);
            ps.setLong(3, threshold);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Could not insert new notification to db", e);
        } finally {
            conn.close();
        }
    }

}
