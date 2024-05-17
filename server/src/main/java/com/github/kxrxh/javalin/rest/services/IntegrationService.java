package com.github.kxrxh.javalin.rest.services;

import com.github.kxrxh.javalin.rest.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class IntegrationService {

    public static void integrateWithBank(Long userId, String bankCredentials) throws SQLException {
        // TODO
        // Logic for integrating with a bank
        // This example is kept simple and may not represent a real integration
    }

    public static void autoCategorizeTransactions(Long userId) throws SQLException {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            Map<String, Long> categoryMapping = loadCategoryMappings(userId, conn);

            String query = "SELECT transaction_id, description FROM transactions WHERE user_id = ? AND category_id IS NULL";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setLong(1, userId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Long transactionId = rs.getLong("transaction_id");
                String description = rs.getString("description");

                Long categoryId = categorizeTransaction(description, categoryMapping);
                if (categoryId != null) {
                    updateTransactionCategory(transactionId, categoryId, conn);
                }
            }
        }
    }

    private static Map <String, Long> loadCategoryMappings(Long userId, Connection conn) throws SQLException {
        Map<String, Long> categoryMapping = new HashMap<>();
        String query = "SELECT name, category_id FROM categories WHERE user_id = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setLong(1, userId);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            categoryMapping.put(rs.getString("name").toLowerCase(), rs.getLong("category_id"));
        }
        return categoryMapping;
    }

    private static Long categorizeTransaction(String description, Map <String, Long> categoryMapping) {
        for (Map.Entry<String, Long> entry : categoryMapping.entrySet()) {
            if (description.toLowerCase().contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    private static void updateTransactionCategory(Long transactionId, Long categoryId, Connection conn) throws SQLException {
        String query = "UPDATE transactions SET category_id = ? WHERE transaction_id = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setLong(1, categoryId);
        ps.setLong(2, transactionId);
        ps.executeUpdate();
    }
}
