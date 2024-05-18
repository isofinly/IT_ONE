package com.github.kxrxh.javalin.rest.util;

import com.github.kxrxh.javalin.rest.database.models.Category;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CategoryTest {

    @Test
    void testNoArgsConstructor() {
        Category category = new Category();
        assertNotNull(category, "Category object should not be null");
    }

    @Test
    void testAllArgsConstructor() {
        UUID categoryId = UUID.randomUUID();
        String name = "Groceries";
        UUID familyId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        Category category = new Category(categoryId, name, familyId, createdAt, updatedAt, lastSyncedAt);
        assertEquals(categoryId, category.getCategoryId());
        assertEquals(name, category.getName());
        assertEquals(familyId, category.getFamilyId());
        assertEquals(createdAt, category.getCreatedAt());
        assertEquals(updatedAt, category.getUpdatedAt());
        assertEquals(lastSyncedAt, category.getLastSyncedAt());
    }

    @Test
    void testBuilder() {
        UUID categoryId = UUID.randomUUID();
        String name = "Utilities";
        UUID familyId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        Category category = Category.builder()
                .categoryId(categoryId)
                .name(name)
                .familyId(familyId)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .lastSyncedAt(lastSyncedAt)
                .build();

        assertEquals(categoryId, category.getCategoryId());
        assertEquals(name, category.getName());
        assertEquals(familyId, category.getFamilyId());
        assertEquals(createdAt, category.getCreatedAt());
        assertEquals(updatedAt, category.getUpdatedAt());
        assertEquals(lastSyncedAt, category.getLastSyncedAt());
    }

    @Test
    void testSettersAndGetters() {
        Category category = new Category();

        UUID categoryId = UUID.randomUUID();
        String name = "Entertainment";
        UUID familyId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        category.setCategoryId(categoryId);
        category.setName(name);
        category.setFamilyId(familyId);
        category.setCreatedAt(createdAt);
        category.setUpdatedAt(updatedAt);
        category.setLastSyncedAt(lastSyncedAt);

        assertEquals(categoryId, category.getCategoryId());
        assertEquals(name, category.getName());
        assertEquals(familyId, category.getFamilyId());
        assertEquals(createdAt, category.getCreatedAt());
        assertEquals(updatedAt, category.getUpdatedAt());
        assertEquals(lastSyncedAt, category.getLastSyncedAt());
    }
}
