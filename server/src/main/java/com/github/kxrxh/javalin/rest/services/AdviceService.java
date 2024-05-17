package com.github.kxrxh.javalin.rest.services;

import com.github.kxrxh.javalin.rest.database.DatabaseManager;
import com.github.kxrxh.javalin.rest.database.models.Transaction;
import com.github.kxrxh.javalin.rest.entities.FinancialAdvice;
import com.github.kxrxh.javalin.rest.entities.FinancialForecast;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdviceService {

    public static FinancialAdvice getFinancialAdvice(Long userId) throws SQLException {
        FinancialAdvice advice = new FinancialAdvice();

        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            // Implement your logic to generate financial advice here
            // For example, fetching recent transactions and analyzing them
            String query = "SELECT * FROM transactions WHERE user_id = ? ORDER BY transaction_date DESC LIMIT 10";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setLong(1, userId);

            ResultSet rs = ps.executeQuery();
            List<Transaction> transactions = new ArrayList<>();
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
            advice.setRecentTransactions(transactions);
            // TODO Add real logic for analyzing transactions
            advice.setAdvice("Consider saving more based on recent spending patterns.");
        }

        return advice;
    }

    public static FinancialForecast getFinancialForecast(Long userId, String dateRange) throws SQLException {
        FinancialForecast forecast = new FinancialForecast();

        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            // Implement your logic to generate financial forecast here
            // For example, projecting future transactions based on past data
            String query = "SELECT * FROM transactions WHERE user_id = ? AND transaction_date BETWEEN ? AND ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setLong(1, userId);

            String[] dates = dateRange.split("to");
            ps.setTimestamp(2, Timestamp.valueOf(dates[0].trim()));
            ps.setTimestamp(3, Timestamp.valueOf(dates[1].trim()));

            ResultSet rs = ps.executeQuery();
            List<Transaction> transactions = new ArrayList<>();
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

            // Example forecast logic: Sum up transactions and project future spending
            long totalAmount = transactions.stream().mapToLong(Transaction::getAmount).sum();
            // TODO Add real logic for projecting future spending
            forecast.setProjectedAmount(totalAmount);
            forecast.setForecastMessage("Based on your past transactions, your projected spending is " + totalAmount);
        }

        return forecast;
    }
}
