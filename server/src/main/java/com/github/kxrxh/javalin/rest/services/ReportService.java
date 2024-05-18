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

    /**
     * Generates a monthly financial report for the specified user, year, and month.
     *
     * @param userId The ID of the user for whom the report is generated.
     * @param year   The year for which the report is generated.
     * @param month  The month for which the report is generated (1-12).
     * @return A map containing the generated report data.
     * @throws SQLException If an SQL error occurs.
     */
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

        conn.close();

        return reportData;
    }

    /**
     * Fetches the transaction summary for the specified user, year, and month.
     *
     * @param conn   The database connection.
     * @param userId The ID of the user.
     * @param year   The year for which the summary is fetched.
     * @param month  The month for which the summary is fetched (1-12).
     * @return A map containing the transaction summary.
     * @throws SQLException If an SQL error occurs.
     */
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
        }
        return summary;
    }

    /**
     * Fetches the expenses by category for the specified user, year, and month.
     *
     * @param conn   The database connection.
     * @param userId The ID of the user.
     * @param year   The year for which the expenses are fetched.
     * @param month  The month for which the expenses are fetched (1-12).
     * @return A map containing the expenses by category.
     * @throws SQLException If an SQL error occurs.
     */
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
        }
        return expensesByCategory;
    }

    /**
     * Calculates the trend in expenses compared to the previous month for the
     * specified user, year, and month.
     *
     * @param conn   The database connection.
     * @param userId The ID of the user.
     * @param year   The year for which the trend is calculated.
     * @param month  The month for which the trend is calculated (1-12).
     * @return The percentage change in expenses compared to the previous month.
     * @throws SQLException If an SQL error occurs.
     */
    private static double calculateTrend(Connection conn, UUID userId, int year, int month) throws SQLException {
        // Compare expenses of the current month with the previous month
        double currentMonthExpenses = fetchTransactionSummary(conn, userId, year, month).get(TOTAL_EXPENSES);
        double previousMonthExpenses = fetchTransactionSummary(conn, userId, year, month - 1).get(TOTAL_EXPENSES);
        if (previousMonthExpenses == 0) {
            return 0; // To avoid division by zero
        }
        return ((currentMonthExpenses - previousMonthExpenses) / previousMonthExpenses) * 100;
    }

    /**
     * Generates a distribution chart of expenses by category.
     *
     * @param expensesByCategory A map containing expenses by category.
     * @param totalExpenses      The total expenses.
     * @return A map containing the expense distribution chart.
     */
    private static Map<String, String> generateExpenseDistributionChart(Map<String, Long> expensesByCategory,
                                                                        long totalExpenses) {
        Map<String, String> chart = new HashMap<>();
        for (Map.Entry<String, Long> entry : expensesByCategory.entrySet()) {
            double percentage = ((double) entry.getValue() / totalExpenses) * 100;
            chart.put(entry.getKey(), String.format("%.2f%%", percentage));
        }
        return chart;
    }

    /**
     * Fetches the top 5 largest expenses for the specified user, year, and month.
     *
     * @param conn   The database connection.
     * @param userId The ID of the user.
     * @param year   The year for which the expenses are fetched.
     * @param month  The month for which the expenses are fetched (1-12).
     * @return A list containing maps with description and amount of the largest
     * expenses.
     * @throws SQLException If an SQL error occurs.
     */
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
        }
        return largestExpenses;
    }

    /**
     * Formats the given amount into a string with currency symbol.
     *
     * @param amount The amount to format.
     * @return The formatted amount with currency symbol.
     */
    private static String formatAmount(long amount) {
        return String.format("%s RUB", amount < 0 ? "-" + Math.abs(amount) : amount);
    }

    /**
     * Formats expenses by category into a map with formatted amounts.
     *
     * @param expensesByCategory A map containing expenses by category.
     * @return A map containing formatted expenses by category.
     */
    private static Map<String, String> formatExpensesByCategory(Map<String, Long> expensesByCategory) {
        Map<String, String> formattedExpenses = new HashMap<>();
        for (Map.Entry<String, Long> entry : expensesByCategory.entrySet()) {
            formattedExpenses.put(entry.getKey(), formatAmount(entry.getValue()));
        }
        return formattedExpenses;
    }

    /**
     * Gets the name of the month for the given month number (1-12).
     *
     * @param month The month number (1-12).
     * @return The name of the month.
     */
    private static String getMonthName(int month) {
        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September",
                "October", "November", "December"};
        return monthNames[(month - 1 + 12) % 12];
    }
}
