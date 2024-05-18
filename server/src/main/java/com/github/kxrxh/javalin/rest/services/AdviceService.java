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

    /**
     * Retrieves financial advice for a given user based on recent transactions.
     *
     * @param userId The UUID of the user for whom the financial advice is
     *               requested.
     * @return FinancialAdvice object containing the generated financial advice.
     * @throws SQLException If an SQL exception occurs while fetching recent
     *                      transactions.
     */
    public static FinancialAdvice getFinancialAdvice(UUID userId) throws SQLException {
        FinancialAdvice advice = new FinancialAdvice();

        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();

        if (opConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = opConn.get();

        // Fetch recent transactions based on user_id
        String query = "SELECT t.* FROM transactions t " +
                "JOIN accounts a ON t.account_id = a.account_id " +
                "WHERE a.user_id = ? " +
                "ORDER BY t.date DESC LIMIT 10";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setObject(1, userId, java.sql.Types.OTHER);

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
                        .categoryId(rs.getString(CATEGORY_ID) != null ? UUID.fromString(rs.getString(CATEGORY_ID)) : null)
                        .excluded(rs.getBoolean("excluded"))
                        .notes(rs.getString("notes"))
                        .transactionType(TransactionType.valueOf(rs.getString("transaction_type").toUpperCase()))
                        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                        .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                        .lastSyncedAt(rs.getTimestamp(LAST_SYNCED_AT) != null ? rs.getTimestamp(LAST_SYNCED_AT).toLocalDateTime() : null)
                        .build();
                transactions.add(transaction);
            }
            advice.setRecentTransactions(transactions);
            advice.setAdvice("Consider saving more based on recent spending patterns.");
        } catch (SQLException e) {
            throw new SQLException("Error while fetching recent transactions", e);
        } finally {
            conn.close();
        }

        return advice;
    }

    /**
     * Retrieves a financial forecast for a given user within a specified date
     * range.
     *
     * @param userId    The UUID of the user for whom the financial forecast is
     *                  requested.
     * @param dateRange The date range in the format "start_date to end_date".
     * @return FinancialForecast object containing the generated financial forecast.
     * @throws SQLException If an SQL exception occurs while fetching recent
     *                      transactions.
     */
    public static FinancialForecast getFinancialForecast(UUID userId, String dateRange) throws SQLException {
        FinancialForecast forecast = new FinancialForecast();

        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();
        if (opConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = opConn.get();

        String query = "SELECT transaction_id, name, date, amount, currency, account_id, category_id, excluded, notes, transaction_type, created_at, updated_at, last_synced_at FROM transactions WHERE account_id IN (SELECT account_id FROM accounts WHERE user_id = ?) AND date BETWEEN ? AND ?";
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
                        .categoryId(rs.getString(CATEGORY_ID) != null ? UUID.fromString(rs.getString(CATEGORY_ID))
                                : null)
                        .excluded(rs.getBoolean("excluded"))
                        .notes(rs.getString("notes"))
                        .transactionType(TransactionType.valueOf(rs.getString("transaction_type").toUpperCase()))
                        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                        .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                        .lastSyncedAt(rs.getTimestamp(LAST_SYNCED_AT) != null
                                ? rs.getTimestamp(LAST_SYNCED_AT).toLocalDateTime()
                                : null)
                        .build();
                transactions.add(transaction);
            }

            // Example forecast logic: Sum up transactions and project future spending
            long totalAmount = transactions.stream().mapToLong(Transaction::getAmount).sum();
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
