package com.github.kxrxh.javalin.rest.services;

import com.github.kxrxh.javalin.rest.database.DatabaseManager;
import com.github.kxrxh.javalin.rest.database.GettingConnectionException;
import com.github.kxrxh.javalin.rest.database.models.Transaction;
import com.github.kxrxh.javalin.rest.database.models.Transaction.TransactionType;
import com.github.kxrxh.javalin.rest.entities.BudgetAnalysisResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BudgetService {

    private BudgetService() {
    }

    public static void setBudgetAlert(UUID budgetId, long alertThreshold) throws SQLException {
        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();

        if (opConn.isEmpty()) {
            throw new GettingConnectionException();
        }

        Connection conn = opConn.get();

        String query = "UPDATE budgets SET alert_threshold = ? WHERE budget_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setLong(1, alertThreshold);
            ps.setObject(2, budgetId, java.sql.Types.OTHER);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Could not set budget alert threshold", e);
        }
    }

    public static BudgetAnalysisResult analyzeBudget(UUID budgetId) throws SQLException {
        BudgetAnalysisResult result = new BudgetAnalysisResult();
        List<Transaction> transactions = new ArrayList<>();

        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();
        if (opConn.isEmpty()) {
            throw new GettingConnectionException();
        }

        Connection conn = opConn.get();

        String query = "SELECT * FROM transactions WHERE category_id IN (SELECT category_id FROM budgets WHERE budget_id = ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setObject(1, budgetId, java.sql.Types.OTHER);

            ResultSet rs = ps.executeQuery();
            long totalSpent = 0;
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
                totalSpent += transaction.getAmount();
            }

            result.setTransactions(transactions);
            result.setTotalSpent(totalSpent);

            String budgetQuery = "SELECT limit_amount FROM budgets WHERE budget_id = ?";
            try (PreparedStatement ps2 = conn.prepareStatement(budgetQuery)) {
                ps2.setObject(1, budgetId, java.sql.Types.OTHER);
                rs = ps2.executeQuery();

                if (rs.next()) {
                    result.setBudgetLimit(rs.getLong("limit_amount"));
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Could not analyze budget", e);
        }
        return result;
    }
}
