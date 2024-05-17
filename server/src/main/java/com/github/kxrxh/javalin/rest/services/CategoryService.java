package com.github.kxrxh.javalin.rest.services;

import com.github.kxrxh.javalin.rest.database.DatabaseManager;
import com.github.kxrxh.javalin.rest.database.models.Transaction;
import com.github.kxrxh.javalin.rest.entities.CategoryAnalysisResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryService {

    public static CategoryAnalysisResult analyzeCategory(Long categoryId, String dateRange) throws SQLException {
        CategoryAnalysisResult result = new CategoryAnalysisResult();
        List<Transaction> transactions = new ArrayList<>();

        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            StringBuilder query = new StringBuilder("SELECT * FROM transactions WHERE category_id = ?");
            List<Object> params = new ArrayList<>();
            params.add(categoryId);

            if (dateRange != null) {
                query.append(" AND transaction_date BETWEEN ? AND ?");
                String[] dates = dateRange.split("to");
                params.add(Timestamp.valueOf(dates[0].trim()));
                params.add(Timestamp.valueOf(dates[1].trim()));
            }

            PreparedStatement ps = conn.prepareStatement(query.toString());
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            long totalAmount = 0;
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
                totalAmount += transaction.getAmount();
            }

            result.setTransactions(transactions);
            result.setTotalAmount(totalAmount);
        }

        return result;
    }
}
