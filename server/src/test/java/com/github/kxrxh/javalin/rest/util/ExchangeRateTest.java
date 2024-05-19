package com.github.kxrxh.javalin.rest.util;

import com.github.kxrxh.javalin.rest.database.models.ExchangeRate;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeRateTest {

    @Test
    void testNoArgsConstructor() {
        ExchangeRate exchangeRate = new ExchangeRate();
        assertNotNull(exchangeRate, "ExchangeRate object should not be null");
    }

    @Test
    void testAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        String baseCurrency = "USD";
        String convertedCurrency = "EUR";
        long rate = 0L;
        LocalDate date = LocalDate.now();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        ExchangeRate exchangeRate = new ExchangeRate(id, baseCurrency, convertedCurrency, rate, date, createdAt, updatedAt);
        assertEquals(id, exchangeRate.getId());
        assertEquals(baseCurrency, exchangeRate.getBaseCurrency());
        assertEquals(convertedCurrency, exchangeRate.getConvertedCurrency());
        assertEquals(rate, exchangeRate.getRate());
        assertEquals(date, exchangeRate.getDate());
        assertEquals(createdAt, exchangeRate.getCreatedAt());
        assertEquals(updatedAt, exchangeRate.getUpdatedAt());
    }

    @Test
    void testBuilder() {
        UUID id = UUID.randomUUID();
        String baseCurrency = "GBP";
        String convertedCurrency = "USD";
        long rate = 0L;
        LocalDate date = LocalDate.now();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        ExchangeRate exchangeRate = ExchangeRate.builder()
                .id(id)
                .baseCurrency(baseCurrency)
                .convertedCurrency(convertedCurrency)
                .rate(rate)
                .date(date)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        assertEquals(id, exchangeRate.getId());
        assertEquals(baseCurrency, exchangeRate.getBaseCurrency());
        assertEquals(convertedCurrency, exchangeRate.getConvertedCurrency());
        assertEquals(rate, exchangeRate.getRate());
        assertEquals(date, exchangeRate.getDate());
        assertEquals(createdAt, exchangeRate.getCreatedAt());
        assertEquals(updatedAt, exchangeRate.getUpdatedAt());
    }

    @Test
    void testSettersAndGetters() {
        ExchangeRate exchangeRate = new ExchangeRate();

        UUID id = UUID.randomUUID();
        String baseCurrency = "EUR";
        String convertedCurrency = "JPY";
        long rate = 0L;
        LocalDate date = LocalDate.now();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        exchangeRate.setId(id);
        exchangeRate.setBaseCurrency(baseCurrency);
        exchangeRate.setConvertedCurrency(convertedCurrency);
        exchangeRate.setRate(rate);
        exchangeRate.setDate(date);
        exchangeRate.setCreatedAt(createdAt);
        exchangeRate.setUpdatedAt(updatedAt);

        assertEquals(id, exchangeRate.getId());
        assertEquals(baseCurrency, exchangeRate.getBaseCurrency());
        assertEquals(convertedCurrency, exchangeRate.getConvertedCurrency());
        assertEquals(rate, exchangeRate.getRate());
        assertEquals(date, exchangeRate.getDate());
        assertEquals(createdAt, exchangeRate.getCreatedAt());
        assertEquals(updatedAt, exchangeRate.getUpdatedAt());
    }


    @Test
    void testToString() {
        UUID id = UUID.randomUUID();
        LocalDate date = LocalDate.now();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        ExchangeRate exchangeRate = new ExchangeRate(id, "USD", "EUR", 0L, date, createdAt, updatedAt);

        String expectedString = "ExchangeRate(id=" + id + ", baseCurrency=USD, convertedCurrency=EUR, rate=0, date=" + date +
                ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ")";
        assertEquals(expectedString, exchangeRate.toString());
    }
}
