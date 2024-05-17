package com.github.kxrxh.javalin.rest.services;

import com.github.kxrxh.javalin.rest.database.DatabaseManager;
import com.github.kxrxh.javalin.rest.database.GettingConnectionException;
import com.github.kxrxh.javalin.rest.database.models.Transaction;
import com.github.kxrxh.javalin.rest.database.models.Transaction.TransactionType;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class TransactionService {

    private TransactionService() {
    }

    public static List<Transaction> searchTransactions(UUID userId, String amountRange, String dateRange,
            UUID categoryId, String description) throws SQLException {

        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();
        if (opConn.isEmpty()) {
            throw new GettingConnectionException();
        }

        Connection conn = opConn.get();

        StringBuilder query = new StringBuilder(
                "SELECT * FROM transactions WHERE account_id IN (SELECT account_id FROM accounts WHERE user_id = ?)");
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (amountRange != null) {
            query.append(" AND amount BETWEEN ? AND ?");
            String[] amounts = amountRange.split("-");
            params.add(Long.parseLong(amounts[0]));
            params.add(Long.parseLong(amounts[1]));
        }

        if (dateRange != null) {
            query.append(" AND date BETWEEN ? AND ?");
            String[] dates = dateRange.split("to");
            params.add(Timestamp.valueOf(LocalDateTime.parse(dates[0].trim())));
            params.add(Timestamp.valueOf(LocalDateTime.parse(dates[1].trim())));
        }

        if (categoryId != null) {
            query.append(" AND category_id = ?");
            params.add(categoryId);
        }

        if (description != null) {
            query.append(" AND name LIKE ?");
            params.add("%" + description + "%");
        }

        List<Transaction> transactions = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(query.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Transaction transaction = Transaction.builder()
                        .transactionId(UUID.fromString(rs.getString("transaction_id")))
                        .name(rs.getString("name"))
                        .date(rs.getTimestamp("date").toLocalDateTime())
                        .amount(rs.getLong("amount"))
                        .currency(rs.getString("currency"))
                        .accountId(UUID.fromString(rs.getString("account_id")))
                        .categoryId(rs.getString("category_id") != null ? UUID.fromString(rs.getString("category_id"))
                                : null)
                        .excluded(rs.getBoolean("excluded"))
                        .notes(rs.getString("notes"))
                        .transactionType(TransactionType.valueOf(rs.getString("transaction_type").toUpperCase()))
                        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                        .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                        .lastSyncedAt(rs.getTimestamp("last_synced_at") != null
                                ? rs.getTimestamp("last_synced_at").toLocalDateTime()
                                : null)
                        .build();
                transactions.add(transaction);
            }
        }
        return transactions;
    }

    public static void createRecurringTransaction(UUID userId, long amount, UUID categoryId, String description,
            long frequency) throws SQLException {
        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();
        if (opConn.isEmpty()) {
            throw new GettingConnectionException();
        }

        Connection conn = opConn.get();
        String query = "INSERT INTO recurring_transactions (user_id, amount, category_id, description, frequency) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setObject(1, userId, java.sql.Types.OTHER);
            ps.setLong(2, amount);
            ps.setObject(3, categoryId, java.sql.Types.OTHER);
            ps.setString(4, description);
            ps.setLong(5, frequency);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Could not create recurring transaction", e);
        }
    }
}
