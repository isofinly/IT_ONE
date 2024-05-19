package com.github.kxrxh.javalin.rest.util;

import com.github.kxrxh.javalin.rest.database.models.Category;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void testEqualsAndHashCode() {
        UUID categoryId = UUID.randomUUID();
        UUID familyId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        Category category1 = new Category(categoryId, "Groceries", familyId, createdAt, updatedAt, lastSyncedAt);
        Category category2 = new Category(categoryId, "Groceries", familyId, createdAt, updatedAt, lastSyncedAt);

        assertEquals(category1, category2);
        assertEquals(category1.hashCode(), category2.hashCode());

        category2.setName("Utilities");
        assertNotEquals(category1, category2);
        assertNotEquals(category1.hashCode(), category2.hashCode());
    }

    @Test
    void testToString() {
        UUID categoryId = UUID.randomUUID();
        UUID familyId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastSyncedAt = LocalDateTime.now();

        Category category = new Category(categoryId, "Groceries", familyId, createdAt, updatedAt, lastSyncedAt);
        String expectedString = "Category(categoryId=" + categoryId.toString() + ", name=Groceries, familyId=" + familyId.toString() +
                ", createdAt=" + createdAt + ", updatedAt=" + updatedAt +
                ", lastSyncedAt=" + lastSyncedAt + ")";

        assertEquals(expectedString, category.toString());
    }
}
