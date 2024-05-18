package com.github.kxrxh.javalin.rest.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.github.kxrxh.javalin.rest.database.DatabaseManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CurrencyConversion {

    private static final String BASE_CURRENCY = "RUB";
    private static CurrencyConversion instance;
    private final Map<String, Double> exchangeRates;

    // Private constructor to prevent instantiation
    private CurrencyConversion() {
        exchangeRates = new HashMap<>();
    }

    // Synchronized method to control simultaneous access
    public static synchronized CurrencyConversion getInstance() {
        if (instance == null) {
            instance = new CurrencyConversion();
        }
        return instance;
    }

    public synchronized void syncRates() {
        loadExchangeRates();
    }

    private void loadExchangeRates() {
        // try to load exchange rates from database
        if (!loadExchangeRatesFromDB()) {
            // load exchange rates from CSV
            String csvFilePath = System.getenv("EXCHANGE_RATES_CSV") != null ? System.getenv("EXCHANGE_RATES_CSV")
                    : "./data/EXCHANGE_RATES.csv";
            loadExchangeRatesFromCSV(csvFilePath);
        }
    }

    public synchronized void putExchangeRate(String fromCurrency, String toCurrency, double rate) {
        exchangeRates.put(fromCurrency + "_" + toCurrency, rate);
    }

    public synchronized void clearExchangeRates() {
        exchangeRates.clear();
    }

    private boolean loadExchangeRatesFromDB() {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            log.warn("No database connection available");
            return false;
        }

        try (Connection conn = optConn.get();
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT base_currency, converted_currency, rate FROM exchange_rates WHERE date = CURRENT_DATE");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String currencyPair = rs.getString("base_currency") + "_" + rs.getString("converted_currency");
                double rate = rs.getDouble("rate");
                synchronized (exchangeRates) {
                    exchangeRates.put(currencyPair, rate);
                }
            }
            return true;
        } catch (SQLException e) {
            log.warn("Error loading exchange rates from database", e);
            return false;
        }
    }

    private void loadExchangeRatesFromCSV(String csvFilePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(Paths.get(csvFilePath).toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length != 2) {
                    continue; // skip invalid lines
                }
                String currencyPair = values[0];
                double rate = Double.parseDouble(values[1]);
                synchronized (exchangeRates) {
                    exchangeRates.put(currencyPair, rate);
                }
            }
        } catch (IOException e) {
            log.warn("Error loading exchange rates from CSV", e);
            System.exit(1);
        }
    }

    public double convert(double amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }

        // if exchange rates are not loaded, synchronize them
        if (exchangeRates.keySet().isEmpty()) {
            log.info("Synchronizing exchange rates");
            syncRates();
        }

        String directPair = fromCurrency + "_" + toCurrency;
        String inversePair = toCurrency + "_" + fromCurrency;
        String toBasePair = fromCurrency + "_" + BASE_CURRENCY;
        String fromBasePair = BASE_CURRENCY + "_" + toCurrency;

        synchronized (exchangeRates) {
            Double directRate = exchangeRates.get(directPair);
            if (directRate != null) {
                return amount * directRate;
            }

            Double inverseRate = exchangeRates.get(inversePair);
            if (inverseRate != null) {
                return amount / inverseRate;
            }

            Double toBaseRate = exchangeRates.get(toBasePair);
            Double fromBaseRate = exchangeRates.get(fromBasePair);
            if (toBaseRate != null && fromBaseRate != null) {
                double baseAmount = amount * toBaseRate;
                return baseAmount * fromBaseRate;
            }
        }

        throw new IllegalArgumentException(
                "Exchange rate not found for currency pair: " + fromCurrency + " to " + toCurrency);
    }
}
