package com.github.kxrxh.javalin.rest.services;
import com.github.kxrxh.javalin.rest.database.DatabaseManager;
import com.github.kxrxh.javalin.rest.database.models.RecurringTransaction;
import java.util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;

public class RecurringTransactionService {

    private RecurringTransactionService() {
    }

    public static void createRecurringTransaction(UUID userId, long amount, UUID categoryId, String description, long frequency) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new SQLException("Could not get connection from pool");
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO recurring_transactions (recurring_transaction_id, user_id, amount, category_id, description, frequency, created_at, updated_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")) {
            ps.setObject(1, UUID.randomUUID(), java.sql.Types.OTHER);
            ps.setObject(2, userId, java.sql.Types.OTHER);
            ps.setLong(3, amount);
            ps.setObject(4, categoryId, java.sql.Types.OTHER);
            ps.setString(5, description);
            ps.setLong(6, frequency);
            ps.executeUpdate();
        }
    }

    public static RecurringTransaction readRecurringTransaction(UUID userId, UUID recurringTransactionId) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new SQLException("Could not get connection from pool");
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT rt.*, c.name AS category_name, u.family_id, f.name AS family_name " +
                        "FROM recurring_transactions rt " +
                        "LEFT JOIN categories c ON rt.category_id = c.category_id " +
                        "JOIN users u ON rt.user_id = u.user_id " +
                        "LEFT JOIN families f ON u.family_id = f.family_id " +
                        "WHERE rt.recurring_transaction_id = ? AND rt.user_id = ?")) {
            ps.setObject(1, recurringTransactionId, java.sql.Types.OTHER);
            ps.setObject(2, userId, java.sql.Types.OTHER);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new RecurringTransaction(
                            UUID.fromString(rs.getString("recurring_transaction_id")),
                            UUID.fromString(rs.getString("user_id")),
                            rs.getLong("amount"),
                            rs.getObject("category_id") != null ? UUID.fromString(rs.getString("category_id")) : null,
                            rs.getString("category_name"),
                            rs.getString("description"),
                            rs.getLong("frequency"),
                            UUID.fromString(rs.getString("family_id")),
                            rs.getString("family_name"),
                            rs.getTimestamp("created_at").toLocalDateTime(),
                            rs.getTimestamp("updated_at").toLocalDateTime()
                    );
                } else {
                    throw new SQLException("Recurring transaction not found");
                }
            }
        }
    }

    public static void updateRecurringTransaction(UUID userId, UUID recurringTransactionId, long amount, UUID categoryId, String description, long frequency) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new SQLException("Could not get connection from pool");
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE recurring_transactions SET amount = ?, category_id = ?, description = ?, frequency = ?, updated_at = CURRENT_TIMESTAMP " +
                        "WHERE recurring_transaction_id = ? AND user_id = ?")) {
            ps.setLong(1, amount);
            ps.setObject(2, categoryId, java.sql.Types.OTHER);
            ps.setString(3, description);
            ps.setLong(4, frequency);
            ps.setObject(5, recurringTransactionId, java.sql.Types.OTHER);
            ps.setObject(6, userId, java.sql.Types.OTHER);
            ps.executeUpdate();
        }
    }

    public static void deleteRecurringTransaction(UUID userId, UUID recurringTransactionId) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new SQLException("Could not get connection from pool");
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM recurring_transactions WHERE recurring_transaction_id = ? AND user_id = ?")) {
            ps.setObject(1, recurringTransactionId, java.sql.Types.OTHER);
            ps.setObject(2, userId, java.sql.Types.OTHER);
            ps.executeUpdate();
        }
    }
}
