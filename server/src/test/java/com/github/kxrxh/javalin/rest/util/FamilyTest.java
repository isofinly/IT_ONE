package com.github.kxrxh.javalin.rest.util;

import com.github.kxrxh.javalin.rest.database.models.Family;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FamilyTest {

    @Test
    void testNoArgsConstructor() {
        Family family = new Family();
        assertNotNull(family, "Family object should not be null");
    }

    @Test
    void testAllArgsConstructor() {
        UUID familyId = UUID.randomUUID();
        String name = "Smith Family";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        String currency = "USD";

        Family family = new Family(familyId, name, createdAt, updatedAt, currency);
        assertEquals(familyId, family.getFamilyId());
        assertEquals(name, family.getName());
        assertEquals(createdAt, family.getCreatedAt());
        assertEquals(updatedAt, family.getUpdatedAt());
        assertEquals(currency, family.getCurrency());
    }

    @Test
    void testBuilder() {
        UUID familyId = UUID.randomUUID();
        String name = "Doe Family";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        String currency = "EUR";

        Family family = Family.builder()
                .familyId(familyId)
                .name(name)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .currency(currency)
                .build();

        assertEquals(familyId, family.getFamilyId());
        assertEquals(name, family.getName());
        assertEquals(createdAt, family.getCreatedAt());
        assertEquals(updatedAt, family.getUpdatedAt());
        assertEquals(currency, family.getCurrency());
    }

    @Test
    void testSettersAndGetters() {
        Family family = new Family();

        UUID familyId = UUID.randomUUID();
        String name = "Johnson Family";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        String currency = "GBP";

        family.setFamilyId(familyId);
        family.setName(name);
        family.setCreatedAt(createdAt);
        family.setUpdatedAt(updatedAt);
        family.setCurrency(currency);

        assertEquals(familyId, family.getFamilyId());
        assertEquals(name, family.getName());
        assertEquals(createdAt, family.getCreatedAt());
        assertEquals(updatedAt, family.getUpdatedAt());
        assertEquals(currency, family.getCurrency());
    }
}
