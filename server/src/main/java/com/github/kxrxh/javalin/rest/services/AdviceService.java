package com.github.kxrxh.javalin.rest.services;

import com.github.kxrxh.javalin.rest.database.ConnectionRetrievingException;
import com.github.kxrxh.javalin.rest.database.DatabaseManager;
import com.github.kxrxh.javalin.rest.database.models.Transaction;
import com.github.kxrxh.javalin.rest.database.models.Transaction.TransactionType;
import com.github.kxrxh.javalin.rest.entities.FinancialAdvice;
import com.github.kxrxh.javalin.rest.entities.FinancialForecast;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AdviceService extends AbstractService {

    private AdviceService() {
    }

    public static FinancialAdvice getFinancialAdvice(UUID userId) throws SQLException {
        FinancialAdvice advice = new FinancialAdvice();

        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();

        if (opConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = opConn.get();

        // Implement your logic to generate financial advice here
        // For example, fetching recent transactions and analyzing them
        String query = "SELECT * FROM transactions WHERE account_id = ? ORDER BY transaction_date DESC LIMIT 10";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, userId.toString());

            ResultSet rs = ps.executeQuery();
            List<Transaction> transactions = new ArrayList<>();
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
            // TODO Add real logic for analyzing transactions
            advice.setRecentTransactions(transactions);
            advice.setAdvice("Consider saving more based on recent spending patterns.");
        } catch (SQLException e) {
            throw new SQLException("Error while fetching recent transactions", e);
        } finally {
            conn.close();
        }

        return advice;
    }

    public static FinancialForecast getFinancialForecast(UUID userId, String dateRange) throws SQLException {
        FinancialForecast forecast = new FinancialForecast();

        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();
        if (opConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = opConn.get();

        String query = "SELECT * FROM transactions WHERE account_id IN (SELECT account_id FROM accounts WHERE user_id = ?) AND date BETWEEN ? AND ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setObject(1, userId, java.sql.Types.OTHER);

            String[] dates = dateRange.split("to");
            ps.setTimestamp(2, Timestamp.valueOf(dates[0].trim()));
            ps.setTimestamp(3, Timestamp.valueOf(dates[1].trim()));

            ResultSet rs = ps.executeQuery();
            List<Transaction> transactions = new ArrayList<>();
            while (rs.next()) {
                Transaction transaction = Transaction.builder()
                        .transactionId(UUID.fromString(rs.getString("transaction_id")))
                        .name(rs.getString("name"))
                        .date(rs.getTimestamp("date").toLocalDateTime())
                        .amount(rs.getLong("amount"))
                        .currency(rs.getString("currency"))
                        .accountId(UUID.fromString(rs.getString("account_id")))
                        .categoryId(rs.getString("category_id") != null ? UUID.fromString(rs.getString("category_id")) : null)
                        .excluded(rs.getBoolean("excluded"))
                        .notes(rs.getString("notes"))
                        .transactionType(TransactionType.valueOf(rs.getString("transaction_type").toUpperCase()))
                        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                        .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                        .lastSyncedAt(rs.getTimestamp("last_synced_at") != null ? rs.getTimestamp("last_synced_at").toLocalDateTime() : null)
                        .build();
                transactions.add(transaction);
            }

            // Example forecast logic: Sum up transactions and project future spending
            long totalAmount = transactions.stream().mapToLong(Transaction::getAmount).sum();
            // TODO Add real logic for projecting future spending
            forecast.setProjectedAmount(totalAmount);
            forecast.setForecastMessage("Based on your past transactions, your projected spending is " + totalAmount);
        } catch (SQLException e) {
            throw new SQLException("Error while fetching recent transactions", e);
        } finally {
            conn.close();
        }
        return forecast;
    }

}
