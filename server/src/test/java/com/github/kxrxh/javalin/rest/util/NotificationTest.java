package com.github.kxrxh.javalin.rest.util;

import com.github.kxrxh.javalin.rest.database.models.Notification;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class NotificationTest {

    @Test
    void testNoArgsConstructor() {
        Notification notification = new Notification();
        assertNotNull(notification, "Notification object should not be null");
    }

    @Test
    void testAllArgsConstructor() {
        UUID notificationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String notificationType = "Email";
        long threshold = 1000L;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        Notification notification = new Notification(notificationId, userId, notificationType, threshold, createdAt, updatedAt, lastSyncedAt);
        assertEquals(notificationId, notification.getNotificationId());
        assertEquals(userId, notification.getUserId());
        assertEquals(notificationType, notification.getNotificationType());
        assertEquals(threshold, notification.getThreshold());
        assertEquals(createdAt, notification.getCreatedAt());
        assertEquals(updatedAt, notification.getUpdatedAt());
        assertEquals(lastSyncedAt, notification.getLastSyncedAt());
    }

    @Test
    void testBuilder() {
        UUID notificationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String notificationType = "SMS";
        long threshold = 500L;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        Notification notification = Notification.builder()
                .notificationId(notificationId)
                .userId(userId)
                .notificationType(notificationType)
                .threshold(threshold)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .lastSyncedAt(lastSyncedAt)
                .build();

        assertEquals(notificationId, notification.getNotificationId());
        assertEquals(userId, notification.getUserId());
        assertEquals(notificationType, notification.getNotificationType());
        assertEquals(threshold, notification.getThreshold());
        assertEquals(createdAt, notification.getCreatedAt());
        assertEquals(updatedAt, notification.getUpdatedAt());
        assertEquals(lastSyncedAt, notification.getLastSyncedAt());
    }

    @Test
    void testSettersAndGetters() {
        Notification notification = new Notification();

        UUID notificationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String notificationType = "Push";
        long threshold = 2000L;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        notification.setNotificationId(notificationId);
        notification.setUserId(userId);
        notification.setNotificationType(notificationType);
        notification.setThreshold(threshold);
        notification.setCreatedAt(createdAt);
        notification.setUpdatedAt(updatedAt);
        notification.setLastSyncedAt(lastSyncedAt);

        assertEquals(notificationId, notification.getNotificationId());
        assertEquals(userId, notification.getUserId());
        assertEquals(notificationType, notification.getNotificationType());
        assertEquals(threshold, notification.getThreshold());
        assertEquals(createdAt, notification.getCreatedAt());
        assertEquals(updatedAt, notification.getUpdatedAt());
        assertEquals(lastSyncedAt, notification.getLastSyncedAt());
    }
}
