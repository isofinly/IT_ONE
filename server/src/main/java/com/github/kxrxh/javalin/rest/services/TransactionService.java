package com.github.kxrxh.javalin.rest.services;

import com.github.kxrxh.javalin.rest.database.ConnectionRetrievingException;
import com.github.kxrxh.javalin.rest.database.DatabaseManager;
import com.github.kxrxh.javalin.rest.database.models.Transaction;
import com.github.kxrxh.javalin.rest.database.models.Transaction.TransactionType;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TransactionService extends AbstractService {

    public static List<Transaction> searchTransactions(UUID userId, String amountRange, String dateRange,
                                                       UUID categoryId, String description) throws SQLException {
        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();
        if (opConn.isEmpty()) {
            throw new ConnectionRetrievingException();
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
            params.add(Timestamp.valueOf(dates[0].trim()));
            params.add(Timestamp.valueOf(dates[1].trim()));
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
        } finally {
            conn.close();
        }
        return transactions;
    }

    public static void createTransaction(UUID userId, UUID accountId, UUID categoryId, long amount, String name,
                                         LocalDateTime date, String currency, String notes, TransactionType transactionType) throws SQLException {
        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();
        if (opConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = opConn.get();
        String query = "INSERT INTO transactions (account_id, category_id, amount, name, date, currency, notes, transaction_type, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setObject(1, accountId, java.sql.Types.OTHER);
            ps.setObject(2, categoryId, java.sql.Types.OTHER);
            ps.setLong(3, amount);
            ps.setString(4, name);
            ps.setTimestamp(5, Timestamp.valueOf(date));
            ps.setString(6, currency);
            ps.setString(7, notes);
            ps.setString(8, transactionType.name().toUpperCase());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Could not create transaction", e);
        } finally {
            conn.close();
        }
    }

    public static void updateTransaction(UUID userId, UUID transactionId, UUID accountId, UUID categoryId, long amount,
                                         String name, LocalDateTime date, String currency, String notes, TransactionType transactionType)
            throws SQLException {
        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();
        if (opConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = opConn.get();
        String query = "UPDATE transactions SET account_id = ?, category_id = ?, amount = ?, name = ?, date = ?, currency = ?, notes = ?, transaction_type = ?, updated_at = CURRENT_TIMESTAMP WHERE transaction_id = ? AND account_id IN (SELECT account_id FROM accounts WHERE user_id = ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setObject(1, accountId, java.sql.Types.OTHER);
            ps.setObject(2, categoryId, java.sql.Types.OTHER);
            ps.setLong(3, amount);
            ps.setString(4, name);
            ps.setTimestamp(5, Timestamp.valueOf(date));
            ps.setString(6, currency);
            ps.setString(7, notes);
            ps.setString(8, transactionType.name().toUpperCase());
            ps.setObject(9, transactionId, java.sql.Types.OTHER);
            ps.setObject(10, userId, java.sql.Types.OTHER);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Could not update transaction", e);
        } finally {
            conn.close();
        }
    }

    public static void deleteTransaction(UUID userId, UUID transactionId) throws SQLException {
        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();
        if (opConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = opConn.get();
        String query = "DELETE FROM transactions WHERE transaction_id = ? AND account_id IN (SELECT account_id FROM accounts WHERE user_id = ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setObject(1, transactionId, java.sql.Types.OTHER);
            ps.setObject(2, userId, java.sql.Types.OTHER);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Could not delete transaction", e);
        } finally {
            conn.close();
        }
    }

    public static void createRecurringTransaction(UUID userId, long amount, UUID categoryId, String description,
                                                  long frequency) throws SQLException {
        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();
        if (opConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = opConn.get();
        String query = "INSERT INTO recurring_transactions (user_id, amount, category_id, description, frequency, created_at, updated_at) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setObject(1, userId, java.sql.Types.OTHER);
            ps.setLong(2, amount);
            ps.setObject(3, categoryId, java.sql.Types.OTHER);
            ps.setString(4, description);
            ps.setLong(5, frequency);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Could not create recurring transaction", e);
        } finally {
            conn.close();
        }
    }
}
