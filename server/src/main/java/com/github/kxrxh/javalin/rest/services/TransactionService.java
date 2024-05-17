package com.github.kxrxh.javalin.rest.services;

import com.github.kxrxh.javalin.rest.database.DatabaseManager;
import com.github.kxrxh.javalin.rest.database.models.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionService {

    public List<Transaction> searchTransactions(Long userId, String amountRange, String dateRange, Long categoryId, String description) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();

        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            StringBuilder query = new StringBuilder("SELECT * FROM transactions WHERE user_id = ?");
            List<Object> params = new ArrayList<>();
            params.add(userId);

            if (amountRange != null) {
                query.append(" AND amount BETWEEN ? AND ?");
                String[] amounts = amountRange.split("-");
                params.add(Long.parseLong(amounts[0]));
                params.add(Long.parseLong(amounts[1]));
            }

            if (dateRange != null) {
                query.append(" AND transaction_date BETWEEN ? AND ?");
                String[] dates = dateRange.split("to");
                params.add(Timestamp.valueOf(dates[0].trim()));
                params.add(Timestamp.valueOf(dates[1].trim()));
            }

            if (categoryId != null) {
                query.append(" AND category_id = ?");
                params.add(categoryId);
            }

            if (description != null) {
                query.append(" AND description LIKE ?");
                params.add("%" + description + "%");
            }

            PreparedStatement ps = conn.prepareStatement(query.toString());
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
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
            }
        }

        return transactions;
    }

    public void createRecurringTransaction(Long userId, Long amount, Long categoryId, String description, String frequency) throws SQLException {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "INSERT INTO recurring_transactions (user_id, amount, category_id, description, frequency) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setLong(1, userId);
            ps.setLong(2, amount);
            ps.setLong(3, categoryId);
            ps.setString(4, description);
            ps.setString(5, frequency);
            ps.executeUpdate();
        }
    }
}
