package com.github.kxrxh.javalin.rest.services;

import com.github.kxrxh.javalin.rest.database.DatabaseManager;
import com.github.kxrxh.javalin.rest.database.models.Transaction;
import com.github.kxrxh.javalin.rest.entities.BudgetAnalysisResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BudgetService {

    public void setBudgetAlert(Long budgetId, Long alertThreshold) throws SQLException {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "UPDATE budgets SET alert_threshold = ? WHERE budget_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setLong(1, alertThreshold);
            ps.setLong(2, budgetId);
            ps.executeUpdate();
        }
    }

    public BudgetAnalysisResult analyzeBudget(Long budgetId) throws SQLException {
        BudgetAnalysisResult result = new BudgetAnalysisResult();
        List<Transaction> transactions = new ArrayList<>();

        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "SELECT * FROM transactions WHERE category_id IN (SELECT category_id FROM budgets WHERE budget_id = ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setLong(1, budgetId);

            ResultSet rs = ps.executeQuery();
            long totalSpent = 0;
            while (rs.next()) {
                Transaction transaction = new Transaction(
                        rs.getLong("transaction_id"),
                        rs.getLong("amount"),
                        rs.getTimestamp("transaction_date"),
                        rs.getLong("category_id"),
                        rs.getLong("user_id"),
                        rs.getString("description")
                );
                transactions.add(transaction);
                totalSpent += transaction.getAmount();
            }

            result.setTransactions(transactions);
            result.setTotalSpent(totalSpent);

            String budgetQuery = "SELECT limit_amount FROM budgets WHERE budget_id = ?";
            ps = conn.prepareStatement(budgetQuery);
            ps.setLong(1, budgetId);
            rs = ps.executeQuery();

            if (rs.next()) {
                result.setBudgetLimit(rs.getLong("limit_amount"));
            }
        }

        return result;
    }
}
