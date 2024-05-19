package com.github.kxrxh.javalin.rest.services;

import com.github.kxrxh.javalin.rest.database.ConnectionRetrievingException;
import com.github.kxrxh.javalin.rest.database.DatabaseManager;
import com.github.kxrxh.javalin.rest.database.models.Valuation;
import com.github.kxrxh.javalin.rest.util.CurrencyConversion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public class ValuationService extends AbstractService {

    private ValuationService() {
    }

    /**
     * Creates a valuation for a specific account.
     *
     * @param userId    The ID of the user creating the valuation.
     * @param accountId The ID of the account for which the valuation is created.
     * @param date      The date of the valuation.
     * @param value     The value of the account.
     * @param currency  The currency of the valuation.
     * @throws SQLException If an SQL error occurs.
     */
    public static void createValuation(UUID userId, UUID accountId, LocalDate date, long value, String currency)
            throws SQLException {
        if (isUserAuthorized(userId, accountId)) {
            throw new SQLException("User not authorized to create valuation for this account");
        }

        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO valuations (id, account_id, date, value, currency, created_at, updated_at) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")) {
            ps.setObject(1, UUID.randomUUID(), java.sql.Types.OTHER);
            ps.setObject(2, accountId, java.sql.Types.OTHER);
            ps.setDate(3, java.sql.Date.valueOf(date));
            ps.setLong(4, value);
            ps.setString(5, currency);
            ps.executeUpdate();
        } finally {
            conn.close();
        }
    }

    /**
     * Reads a valuation for a specific account.
     *
     * @param userId         The ID of the user requesting the valuation.
     * @param valuationId    The ID of the valuation to read.
     * @param targetCurrency The currency in which to convert the valuation value.
     * @return The valuation.
     * @throws SQLException If an SQL error occurs.
     */
    public static Valuation readValuation(UUID userId, UUID valuationId, String targetCurrency) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement("SELECT id, account_id, date, value, currency FROM valuations WHERE id = ?")) {
            ps.setObject(1, valuationId, java.sql.Types.OTHER);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UUID accountId = UUID.fromString(rs.getString(ACCOUNT_ID));
                    if (isUserAuthorized(userId, accountId)) {
                        throw new SQLException("User not authorized to read valuation for this account");
                    }

                    long value = rs.getLong("value");
                    String currency = rs.getString("currency");
                    double convertedValue = CurrencyConversion.getInstance().convert(value, currency, targetCurrency);

                    return Valuation.builder()
                            .id(UUID.fromString(rs.getString("id")))
                            .accountId(accountId).date(rs.getDate("date").toLocalDate())
                            .value((long) convertedValue)
                            .currency(currency)
                            .build();
                } else {
                    throw new SQLException("Valuation not found");
                }
            }
        } finally {
            conn.close();
        }
    }

    /**
     * Updates a valuation for a specific account.
     *
     * @param userId      The ID of the user updating the valuation.
     * @param valuationId The ID of the valuation to update.
     * @param accountId   The ID of the account associated with the valuation.
     * @param date        The new date of the valuation.
     * @param value       The new value of the account.
     * @param currency    The new currency of the valuation.
     * @throws SQLException If an SQL error occurs.
     */
    public static void updateValuation(UUID userId, UUID valuationId, UUID accountId, LocalDate date, long value,
                                       String currency) throws SQLException {
        if (isUserAuthorized(userId, accountId)) {
            throw new SQLException("User not authorized to update valuation for this account");
        }

        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE valuations SET account_id = ?, date = ?, value = ?, currency = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?")) {
            ps.setObject(1, accountId, java.sql.Types.OTHER);
            ps.setDate(2, java.sql.Date.valueOf(date));
            ps.setLong(3, value);
            ps.setString(4, currency);
            ps.setObject(5, valuationId, java.sql.Types.OTHER);
            ps.executeUpdate();
        } finally {
            conn.close();
        }
    }

    /**
     * Deletes a valuation.
     *
     * @param userId      The ID of the user deleting the valuation.
     * @param valuationId The ID of the valuation to delete.
     * @throws SQLException If an SQL error occurs.
     */
    public static void deleteValuation(UUID userId, UUID valuationId) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement("SELECT account_id FROM valuations WHERE id = ?")) {
            ps.setObject(1, valuationId, java.sql.Types.OTHER);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UUID accountId = UUID.fromString(rs.getString("account_id"));
                    if (isUserAuthorized(userId, accountId)) {
                        throw new SQLException("User not authorized to delete valuation for this account");
                    }

                    try (PreparedStatement psDel = conn.prepareStatement("DELETE FROM valuations WHERE id = ?")) {
                        psDel.setObject(1, valuationId, java.sql.Types.OTHER);
                        psDel.executeUpdate();
                    }
                } else {
                    throw new SQLException("Valuation not found");
                }
            }
        } finally {
            conn.close();
        }
    }

    /**
     * Checks if the user is authorized to read valuations for this account.
     *
     * @param userId user id
     * @param accountId account id
     * @return True if the user is authorized to read valuations for this account.
     * False otherwise.
     * @Throws SQLException if an SQL error occurs.
     */
    private static boolean isUserAuthorized(UUID userId, UUID accountId) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn
                .prepareStatement("SELECT user_id, family_id, " +
                        " created_at, updated_at, is_active, last_synced_at " +
                        "FROM accounts WHERE account_id = ? AND user_id = ?")) {
            ps.setObject(1, accountId, java.sql.Types.OTHER);
            ps.setObject(2, userId, java.sql.Types.OTHER);
            try (ResultSet rs = ps.executeQuery()) {
                return !rs.next();
            }
        } finally {
            conn.close();
        }
    }

}
