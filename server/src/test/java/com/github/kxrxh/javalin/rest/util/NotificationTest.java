package com.github.kxrxh.javalin.rest.util;

import com.github.kxrxh.javalin.rest.database.models.Notification;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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
    @Test
    void testEqualsAndHashCode() {
        UUID notificationId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        Notification notification1 = new Notification(notificationId, UUID.randomUUID(), "Email", 1000L, createdAt, updatedAt, LocalDateTime.now());
        Notification notification2 = new Notification(notificationId, UUID.randomUUID(), "Email", 1000L, createdAt, updatedAt, LocalDateTime.now());


        notification2.setNotificationType("SMS");
        assertNotEquals(notification1, notification2);
        assertNotEquals(notification1.hashCode(), notification2.hashCode());
    }

    @Test
    void testToString() {
        UUID notificationId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        Notification notification = new Notification(notificationId, UUID.randomUUID(), "Push", 2000L, createdAt, updatedAt, LocalDateTime.now());

        String expectedString = "Notification(notificationId=" + notificationId + ", userId=" + notification.getUserId() +
                ", notificationType=Push, threshold=2000, createdAt=" + createdAt + ", updatedAt=" + updatedAt +
                ", lastSyncedAt=" + notification.getLastSyncedAt() + ")";
        assertEquals(expectedString, notification.toString());
    }
}
