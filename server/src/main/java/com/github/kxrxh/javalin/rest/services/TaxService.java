package com.github.kxrxh.javalin.rest.services;

import com.github.kxrxh.javalin.rest.database.ConnectionRetrievingException;
import com.github.kxrxh.javalin.rest.database.DatabaseManager;
import com.github.kxrxh.javalin.rest.database.models.Tax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class TaxService extends AbstractService {

    /**
     * Creates a new tax record.
     *
     * @param userId      The ID of the user creating the tax.
     * @param name        The name of the tax.
     * @param description The description of the tax.
     * @param rate        The rate of the tax.
     * @param currency    The currency of the tax.
     * @throws SQLException If an SQL error occurs.
     */
    public static void createTax(String name, String description, long rate, String currency)
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
        } finally {
            conn.close();
        }
    }

    /**
     * Retrieves a tax record by its ID.
     *
     * @param userId The ID of the user retrieving the tax.
     * @param taxId  The ID of the tax to retrieve.
     * @return The Tax object if found.
     * @throws SQLException If the tax is not found or if an SQL error occurs.
     */
    public static Tax readTax(UUID taxId) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement("SELECT id, name, description, rate, currency FROM taxes WHERE id = ?")) {
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
        } finally {
            conn.close();
        }
    }

    /**
     * Updates an existing tax record.
     *
     * @param userId      The ID of the user updating the tax.
     * @param taxId       The ID of the tax to update.
     * @param name        The updated name of the tax.
     * @param description The updated description of the tax.
     * @param rate        The updated rate of the tax.
     * @param currency    The updated currency of the tax.
     * @throws SQLException If an SQL error occurs.
     */
    public static void updateTax(UUID taxId, String name, String description, long rate, String currency)
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
        } finally {
            conn.close();
        }
    }

    /**
     * Deletes a tax record.
     *
     * @param userId The ID of the user deleting the tax.
     * @param taxId  The ID of the tax to delete.
     * @throws SQLException If an SQL error occurs.
     */
    public static void deleteTax(UUID taxId) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM taxes WHERE id = ?")) {
            ps.setObject(1, taxId, java.sql.Types.OTHER);
            ps.executeUpdate();
        } finally {
            conn.close();
        }
    }

    /**
     * Calculates the total taxes for a given user.
     *
     * @param userId The ID of the user to calculate taxes for.
     * @return The total taxes for the user.
     * @throws SQLException If an SQL error occurs.
     */
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
        } finally {
            conn.close();
        }

        return totalTaxes;
    }
}
