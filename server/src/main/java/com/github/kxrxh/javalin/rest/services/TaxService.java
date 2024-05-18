package com.github.kxrxh.javalin.rest.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import com.github.kxrxh.javalin.rest.database.ConnectionRetrievingException;
import com.github.kxrxh.javalin.rest.database.DatabaseManager;
import com.github.kxrxh.javalin.rest.database.models.Tax;

public class TaxService {

    private TaxService() {
    }

    public static void createTax(UUID userId, String name, String description, long rate, String currency)
            throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO taxes (id, name, description, rate, currency, created_at, updated_at) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")) {
            ps.setObject(1, UUID.randomUUID(), java.sql.Types.OTHER);
            ps.setString(2, name);
            ps.setString(3, description);
            ps.setLong(4, rate);
            ps.setString(5, currency);
            ps.executeUpdate();
        }
    }

    public static Tax readTax(UUID userId, UUID taxId) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM taxes WHERE id = ?")) {
            ps.setObject(1, taxId, java.sql.Types.OTHER);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Tax.builder()
                            .id(UUID.fromString(rs.getString("id")))
                            .name(rs.getString("name"))
                            .description(rs.getString("description"))
                            .rate(rs.getLong("rate"))
                            .currency(rs.getString("currency"))
                            .build();
                } else {
                    throw new SQLException("Tax not found");
                }
            }
        }
    }

    public static void updateTax(UUID userId, UUID taxId, String name, String description, long rate, String currency)
            throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE taxes SET name = ?, description = ?, rate = ?, currency = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?")) {
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setLong(3, rate);
            ps.setString(4, currency);
            ps.setObject(5, taxId, java.sql.Types.OTHER);
            ps.executeUpdate();
        }
    }

    public static void deleteTax(UUID userId, UUID taxId) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM taxes WHERE id = ?")) {
            ps.setObject(1, taxId, java.sql.Types.OTHER);
            ps.executeUpdate();
        }
    }

    public static double calculateTaxes(UUID userId) throws SQLException {
        double totalTaxes = 0;

        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT a.balance, t.rate FROM accounts a " +
                        "JOIN taxes t ON a.currency = t.currency " +
                        "WHERE a.user_id = ? AND a.is_active = TRUE")) {
            ps.setObject(1, userId, java.sql.Types.OTHER);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long balance = rs.getLong("balance");
                    double rate = rs.getLong("rate") / 100.0;
                    totalTaxes += balance * rate;
                }
            }
        }

        return totalTaxes;
    }
}
