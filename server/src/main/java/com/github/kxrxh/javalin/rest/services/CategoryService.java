package com.github.kxrxh.javalin.rest.services;

import com.github.kxrxh.javalin.rest.database.DatabaseManager;
import com.github.kxrxh.javalin.rest.database.GettingConnectionException;
import com.github.kxrxh.javalin.rest.database.models.Transaction;
import com.github.kxrxh.javalin.rest.database.models.Transaction.TransactionType;
import com.github.kxrxh.javalin.rest.entities.CategoryAnalysisResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryService {

    private CategoryService() {
    }
    // TODO: Add family if any and more info. 
    public static CategoryAnalysisResult analyzeCategory(UUID categoryId, String dateRange) throws SQLException {
        CategoryAnalysisResult result = new CategoryAnalysisResult();
        List<Transaction> transactions = new ArrayList<>();

        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();
        if (opConn.isEmpty()) {
            throw new GettingConnectionException();
        }

        Connection conn = opConn.get();

        StringBuilder query = new StringBuilder("SELECT * FROM transactions WHERE category_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(categoryId);

        if (dateRange != null) {
            query.append(" AND date BETWEEN ? AND ?");
            String[] dates = dateRange.split("to");
            params.add(Timestamp.valueOf(dates[0].trim() + " 00:00:00"));
            params.add(Timestamp.valueOf(dates[1].trim() + " 23:59:59"));
        }

        try (PreparedStatement ps = conn.prepareStatement(query.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            long totalAmount = 0;
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
                totalAmount += transaction.getAmount();
            }

            result.setTransactions(transactions);
            result.setTotalAmount(totalAmount);
            return result;
        } catch (SQLException e) {
            throw new SQLException("Could not execute query", e);
        }
    }
}