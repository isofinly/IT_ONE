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

    private static final Map<String, Double> exchangeRates = new HashMap<>();
    private static final String BASE_CURRENCY = "RUB";

    static {
        loadExchangeRates();
    }

private static void loadExchangeRates() {
        if (!loadExchangeRatesFromDB()) {
            String csvFilePath = System.getenv("EXCHANGE_RATES_CSV") != null ? System.getenv("EXCHANGE_RATES_CSV")
                : "./data/EXCHANGE_RATES.csv";
        if (csvFilePath != null) {
            loadExchangeRatesFromCSV(csvFilePath);
        } else {
            throw new RuntimeException("Environment variable EXCHANGE_RATES_CSV is not set");
        }
        
    }}


    private static boolean loadExchangeRatesFromDB() {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            return false;
        }

        Connection conn = optConn.get();
        try (PreparedStatement ps = conn.prepareStatement("SELECT base_currency, converted_currency, rate FROM exchange_rates WHERE date = CURRENT_DATE");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String currencyPair = rs.getString("base_currency") + "_" + rs.getString("converted_currency");
                double rate = rs.getDouble("rate");
                exchangeRates.put(currencyPair, rate);
            }
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private static void loadExchangeRatesFromCSV(String csvFilePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(Paths.get(csvFilePath).toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String currencyPair = values[0];
                double rate = Double.parseDouble(values[1]);
                exchangeRates.put(currencyPair, rate);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load exchange rates from CSV file", e);
        }
    }

    public static double convert(double amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }

        String directPair = fromCurrency + "_" + toCurrency;
        Double directRate = exchangeRates.get(directPair);
        if (directRate != null) {
            return amount * directRate;
        }

        String toBasePair = fromCurrency + "_" + BASE_CURRENCY;
        String fromBasePair = BASE_CURRENCY + "_" + toCurrency;
        Double toBaseRate = exchangeRates.get(toBasePair);
        Double fromBaseRate = exchangeRates.get(fromBasePair);

        if (toBaseRate != null && fromBaseRate != null) {
            double baseAmount = amount * toBaseRate;
            return baseAmount * fromBaseRate;
        }

        throw new IllegalArgumentException("Exchange rate not found for currency pair: " + fromCurrency + " to " + toCurrency);
    }
}
