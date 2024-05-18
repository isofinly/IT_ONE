package com.github.kxrxh.javalin.rest.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CurrencyConversionTest {

    private CurrencyConversion currencyConversion;

    @Before
    public void setUp() {
        currencyConversion = CurrencyConversion.getInstance();
        currencyConversion.clearExchangeRates(); // Clear any pre-existing rates for a clean slate
    }

    @Test
    public void testPutAndGetExchangeRate() {
        currencyConversion.putExchangeRate("USD", "EUR", 0.85);
        assertEquals(0.85, currencyConversion.convert(1, "USD", "EUR"), 0.001);
        assertEquals(1 / 0.85, currencyConversion.convert(1, "EUR", "USD"), 0.001);
    }

    @Test
    public void testGetExchangeRate() {
        currencyConversion.putExchangeRate("USD", "EUR", 0.85);
        assertEquals(0.85, currencyConversion.convert(1, "USD", "EUR"), 0.001);
        assertEquals(1 / 0.85, currencyConversion.convert(1, "EUR", "USD"), 0.001);
    }

    @Test
    public void testConvertWithBaseCurrency() {
        currencyConversion.putExchangeRate("USD", "RUB", 75);
        currencyConversion.putExchangeRate("RUB", "EUR", 0.011);

        double amountInEur = currencyConversion.convert(100, "USD", "EUR");
        double expectedAmountInEur = 100 * 75 * 0.011;

        assertEquals(expectedAmountInEur, amountInEur, 0.001);
    }

    @Test
    public void testConvertSameCurrency() {
        assertEquals(100, currencyConversion.convert(100, "USD", "USD"), 0.001);
    }
}
