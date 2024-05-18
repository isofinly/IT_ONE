package com.github.kxrxh.javalin.rest.services;

import com.github.kxrxh.javalin.rest.database.ConnectionRetrievingException;
import com.github.kxrxh.javalin.rest.database.DatabaseManager;
import com.github.kxrxh.javalin.rest.database.models.ExchangeRate;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class ExchangeRateService extends AbstractService {

    private ExchangeRateService() {
    }

    public static void createExchangeRate(String baseCurrency, String convertedCurrency, double rate, Date date)
            throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO exchange_rates (id, base_currency, converted_currency, rate, date, created_at, updated_at) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")) {
            ps.setObject(1, UUID.randomUUID(), java.sql.Types.OTHER);
            ps.setString(2, baseCurrency);
            ps.setString(3, convertedCurrency);
            ps.setDouble(4, rate);
            ps.setDate(5, date);
            ps.executeUpdate();
        } finally {
            conn.close();
        }
    }

    public static ExchangeRate readExchangeRate(String baseCurrency, String convertedCurrency, Date date)
            throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM exchange_rates WHERE base_currency = ? AND converted_currency = ? AND date = ?")) {
            ps.setString(1, baseCurrency);
            ps.setString(2, convertedCurrency);
            ps.setDate(3, date);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ExchangeRate(
                            UUID.fromString(rs.getString("id")),
                            rs.getString("base_currency"),
                            rs.getString("converted_currency"),
                            rs.getLong("rate"),
                            rs.getDate("date").toLocalDate(),
                            rs.getTimestamp("created_at").toLocalDateTime(),
                            rs.getTimestamp("updated_at").toLocalDateTime());
                } else {
                    throw new SQLException("Exchange rate not found");
                }
            }
        } finally {
            conn.close();
        }
    }

    public static void updateExchangeRate(UUID id, String baseCurrency, String convertedCurrency, double rate,
                                          Date date) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE exchange_rates SET base_currency = ?, converted_currency = ?, rate = ?, date = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?")) {
            ps.setString(1, baseCurrency);
            ps.setString(2, convertedCurrency);
            ps.setDouble(3, rate);
            ps.setDate(4, date);
            ps.setObject(5, id, java.sql.Types.OTHER);
            ps.executeUpdate();
        } finally {
            conn.close();
        }
    }

    public static void deleteExchangeRate(UUID id) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM exchange_rates WHERE id = ?")) {
            ps.setObject(1, id, java.sql.Types.OTHER);
            ps.executeUpdate();
        } finally {
            conn.close();
        }
    }
}
