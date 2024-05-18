package com.github.kxrxh.javalin.rest.util;

import com.github.kxrxh.javalin.rest.database.models.Valuation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ValuationTest {

    @Test
    void testNoArgsConstructor() {
        Valuation valuation = new Valuation();
        assertNotNull(valuation, "Valuation object should not be null");
    }

    @Test
    void testAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        LocalDate date = LocalDate.now();
        long value = 1000L;
        String currency = "USD";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        Valuation valuation = new Valuation(id, accountId, date, value, currency, createdAt, updatedAt);
        Assertions.assertEquals(id, valuation.getId());
        Assertions.assertEquals(accountId, valuation.getAccountId());
        Assertions.assertEquals(date, valuation.getDate());
        Assertions.assertEquals(value, valuation.getValue());
        Assertions.assertEquals(currency, valuation.getCurrency());
        Assertions.assertEquals(createdAt, valuation.getCreatedAt());
        Assertions.assertEquals(updatedAt, valuation.getUpdatedAt());
    }

    @Test
    void testBuilder() {
        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        LocalDate date = LocalDate.now();
        long value = 1000L;
        String currency = "USD";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        Valuation valuation = Valuation.builder()
                .id(id)
                .accountId(accountId)
                .date(date)
                .value(value)
                .currency(currency)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        Assertions.assertEquals(id, valuation.getId());
        Assertions.assertEquals(accountId, valuation.getAccountId());
        Assertions.assertEquals(date, valuation.getDate());
        Assertions.assertEquals(value, valuation.getValue());
        Assertions.assertEquals(currency, valuation.getCurrency());
        Assertions.assertEquals(createdAt, valuation.getCreatedAt());
        Assertions.assertEquals(updatedAt, valuation.getUpdatedAt());
    }

    @Test
    void testSettersAndGetters() {
        Valuation valuation = new Valuation();

        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        LocalDate date = LocalDate.now();
        long value = 1000L;
        String currency = "USD";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        valuation.setId(id);
        valuation.setAccountId(accountId);
        valuation.setDate(date);
        valuation.setValue(value);
        valuation.setCurrency(currency);
        valuation.setCreatedAt(createdAt);
        valuation.setUpdatedAt(updatedAt);

        Assertions.assertEquals(id, valuation.getId());
        Assertions.assertEquals(accountId, valuation.getAccountId());
        Assertions.assertEquals(date, valuation.getDate());
        Assertions.assertEquals(value, valuation.getValue());
        Assertions.assertEquals(currency, valuation.getCurrency());
        Assertions.assertEquals(createdAt, valuation.getCreatedAt());
        Assertions.assertEquals(updatedAt, valuation.getUpdatedAt());
    }
}
