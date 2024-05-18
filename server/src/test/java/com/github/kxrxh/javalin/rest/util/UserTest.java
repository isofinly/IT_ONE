package com.github.kxrxh.javalin.rest.util;

import com.github.kxrxh.javalin.rest.database.models.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testNoArgsConstructor() {
        User user = new User();
        assertNotNull(user, "User object should not be null");
    }

    @Test
    void testAllArgsConstructor() {
        UUID userId = UUID.randomUUID();
        UUID familyId = UUID.randomUUID();
        String firstName = "John";
        String lastName = "Doe";
        String email = "john.doe@example.com";
        String passwordDigest = "passwordDigest";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastLogin = LocalDateTime.now();
        boolean active = true;
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        User user = new User(userId, familyId, firstName, lastName, email, passwordDigest, createdAt, updatedAt, lastLogin, active, lastSyncedAt);
        assertEquals(userId, user.getUserId());
        assertEquals(familyId, user.getFamilyId());
        assertEquals(firstName, user.getFirstName());
        assertEquals(lastName, user.getLastName());
        assertEquals(email, user.getEmail());
        assertEquals(passwordDigest, user.getPasswordDigest());
        assertEquals(createdAt, user.getCreatedAt());
        assertEquals(updatedAt, user.getUpdatedAt());
        assertEquals(lastLogin, user.getLastLogin());
        assertTrue(user.isActive());
        assertEquals(lastSyncedAt, user.getLastSyncedAt());
    }

    @Test
    void testBuilder() {
        UUID userId = UUID.randomUUID();
        UUID familyId = UUID.randomUUID();
        String firstName = "Jane";
        String lastName = "Doe";
        String email = "jane.doe@example.com";
        String passwordDigest = "passwordDigest";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastLogin = LocalDateTime.now();
        boolean active = false;
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        User user = User.builder()
                .userId(userId)
                .familyId(familyId)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .passwordDigest(passwordDigest)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .lastLogin(lastLogin)
                .active(active)
                .lastSyncedAt(lastSyncedAt)
                .build();

        assertEquals(userId, user.getUserId());
        assertEquals(familyId, user.getFamilyId());
        assertEquals(firstName, user.getFirstName());
        assertEquals(lastName, user.getLastName());
        assertEquals(email, user.getEmail());
        assertEquals(passwordDigest, user.getPasswordDigest());
        assertEquals(createdAt, user.getCreatedAt());
        assertEquals(updatedAt, user.getUpdatedAt());
        assertEquals(lastLogin, user.getLastLogin());
        assertFalse(user.isActive());
        assertEquals(lastSyncedAt, user.getLastSyncedAt());
    }

    @Test
    void testSettersAndGetters() {
        User user = new User();

        UUID userId = UUID.randomUUID();
        UUID familyId = UUID.randomUUID();
        String firstName = "Alice";
        String lastName = "Smith";
        String email = "alice.smith@example.com";
        String passwordDigest = "passwordDigest";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastLogin = LocalDateTime.now();
        boolean active = true;
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        user.setUserId(userId);
        user.setFamilyId(familyId);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPasswordDigest(passwordDigest);
        user.setCreatedAt(createdAt);
        user.setUpdatedAt(updatedAt);
        user.setLastLogin(lastLogin);
        user.setActive(active);
        user.setLastSyncedAt(lastSyncedAt);

        assertEquals(userId, user.getUserId());
        assertEquals(familyId, user.getFamilyId());
        assertEquals(firstName, user.getFirstName());
        assertEquals(lastName, user.getLastName());
        assertEquals(email, user.getEmail());
        assertEquals(passwordDigest, user.getPasswordDigest());
        assertEquals(createdAt, user.getCreatedAt());
        assertEquals(updatedAt, user.getUpdatedAt());
        assertEquals(lastLogin, user.getLastLogin());
        assertTrue(user.isActive());
        assertEquals(lastSyncedAt, user.getLastSyncedAt());
    }
}
