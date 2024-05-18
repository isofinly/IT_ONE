package com.github.kxrxh.javalin.rest.services;

import com.github.kxrxh.javalin.rest.database.ConnectionRetrievingException;
import com.github.kxrxh.javalin.rest.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ReportService extends AbstractService {

    private ReportService() {
    }

    public static Map<String, Object> generateMonthlyReport(UUID userId, int year, int month) throws SQLException {
        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();

        if (opConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = opConn.get();
        Map<String, Object> reportData = new HashMap<>();

        // 1. Fetch total transactions, expenses, and income
        Map<String, Long> transactionSummary = fetchTransactionSummary(conn, userId, year, month);
        long totalTransactions = transactionSummary.get("totalTransactions");
        long totalExpenses = transactionSummary.get(TOTAL_EXPENSES);
        long totalIncome = transactionSummary.get("totalIncome");
        long netBalance = totalIncome - totalExpenses;

        // 2. Fetch expenses by category
        Map<String, Long> expensesByCategory = fetchExpensesByCategory(conn, userId, year, month);

        // 3. Calculate trend compared to the previous month
        double trend = calculateTrend(conn, userId, year, month);

        // 4. Generate expense distribution chart
        Map<String, String> expenseDistributionChart = generateExpenseDistributionChart(expensesByCategory,
                totalExpenses);

        // 5. Fetch top 5 largest expenses
        List<Map<String, String>> largestExpenses = fetchLargestExpenses(conn, userId, year, month);

        // Populate general information
        Map<String, Object> generalInformation = new HashMap<>();
        generalInformation.put("Period", getMonthName(month) + " " + year);
        generalInformation.put("Total Transactions", totalTransactions);
        generalInformation.put("Total Expenses", formatAmount(totalExpenses));
        generalInformation.put("Total Income", formatAmount(totalIncome));
        generalInformation.put("Net Balance", formatAmount(netBalance));

        // Populate report data
        reportData.put("GeneralInformation", generalInformation);
        reportData.put("ExpensesCategories", formatExpensesByCategory(expensesByCategory));
        reportData.put("ExpenseDistributionChart", expenseDistributionChart);
        reportData.put("LargestExpenses", largestExpenses);
        reportData.put("TrendAnalysis", "Comparison with the previous month (" + getMonthName(month - 1) + " " + year
                + "): Expenses increased by " + trend + "%");

        return reportData;
    }

    private static Map<String, Long> fetchTransactionSummary(Connection conn, UUID userId, int year, int month)
            throws SQLException {
        String query = "SELECT COUNT(*), SUM(CASE WHEN transaction_type = 'inflow' THEN amount ELSE 0 END) AS total_inflow, SUM(CASE WHEN transaction_type = 'outflow' THEN amount ELSE 0 END) AS total_outflow FROM transactions WHERE account_id IN (SELECT account_id FROM accounts WHERE user_id = ?) AND EXTRACT(YEAR FROM date) = ? AND EXTRACT(MONTH FROM date) = ?";
        Map<String, Long> summary = new HashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setObject(1, userId);
            ps.setInt(2, year);
            ps.setInt(3, month);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    summary.put("totalTransactions", rs.getLong(1));
                    summary.put("totalIncome", rs.getLong(2));
                    summary.put(TOTAL_EXPENSES, rs.getLong(3));
                }
            }
        } finally {
            conn.close();
        }
        return summary;
    }

    private static Map<String, Long> fetchExpensesByCategory(Connection conn, UUID userId, int year, int month)
            throws SQLException {
        String query = "SELECT c.name, SUM(t.amount) FROM transactions t INNER JOIN categories c ON t.category_id = c.category_id WHERE account_id IN (SELECT account_id FROM accounts WHERE user_id = ?) AND EXTRACT(YEAR FROM t.date) = ? AND EXTRACT(MONTH FROM t.date) = ? GROUP BY c.name";
        Map<String, Long> expensesByCategory = new HashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setObject(1, userId);
            ps.setInt(2, year);
            ps.setInt(3, month);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    expensesByCategory.put(rs.getString(1), Math.abs(rs.getLong(2)));
                }
            }
        } finally {
            conn.close();
        }
        return expensesByCategory;
    }

    private static double calculateTrend(Connection conn, UUID userId, int year, int month) throws SQLException {
        // Compare expenses of the current month with the previous month
        double currentMonthExpenses = fetchTransactionSummary(conn, userId, year, month).get(TOTAL_EXPENSES);
        double previousMonthExpenses = fetchTransactionSummary(conn, userId, year, month - 1).get(TOTAL_EXPENSES);
        if (previousMonthExpenses == 0) {
            return 0; // To avoid division by zero
        }
        return ((currentMonthExpenses - previousMonthExpenses) / previousMonthExpenses) * 100;
    }

    private static Map<String, String> generateExpenseDistributionChart(Map<String, Long> expensesByCategory,
                                                                        long totalExpenses) {
        Map<String, String> chart = new HashMap<>();
        for (Map.Entry<String, Long> entry : expensesByCategory.entrySet()) {
            double percentage = ((double) entry.getValue() / totalExpenses) * 100;
            chart.put(entry.getKey(), String.format("%.2f%%", percentage));
        }
        return chart;
    }

    private static List<Map<String, String>> fetchLargestExpenses(Connection conn, UUID userId, int year, int month)
            throws SQLException {
        String query = "SELECT name AS description, amount FROM transactions WHERE account_id IN (SELECT account_id FROM accounts WHERE user_id = ?) AND EXTRACT(YEAR FROM date) = ? AND EXTRACT(MONTH FROM date) = ? ORDER BY amount DESC LIMIT 5";
        List<Map<String, String>> largestExpenses = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setObject(1, userId);
            ps.setInt(2, year);
            ps.setInt(3, month);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> expense = new HashMap<>();
                    expense.put(rs.getString(1), formatAmount(rs.getLong(2)));
                    largestExpenses.add(expense);
                }
            }
        } finally {
            conn.close();
        }
        return largestExpenses;
    }

    private static String getMonthName(int month) {
        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September",
                "October", "November", "December"};
        return monthNames[(month - 1 + 12) % 12]; // Ensure month wraps around correctly
    }

    private static String formatAmount(long amount) {
        return String.format("%s RUB", amount < 0 ? "-" + Math.abs(amount) : amount);
    }

    private static Map<String, String> formatExpensesByCategory(Map<String, Long> expensesByCategory) {
        Map<String, String> formattedExpenses = new HashMap<>();
        for (Map.Entry<String, Long> entry : expensesByCategory.entrySet()) {
            formattedExpenses.put(entry.getKey(), formatAmount(entry.getValue()));
        }
        return formattedExpenses;
    }
}
