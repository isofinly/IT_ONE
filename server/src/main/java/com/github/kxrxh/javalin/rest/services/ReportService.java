package com.github.kxrxh.javalin.rest.services;

import com.github.kxrxh.javalin.rest.database.DatabaseManager;
import com.github.kxrxh.javalin.rest.database.GettingConnectionException;
import com.github.kxrxh.javalin.rest.database.models.Report;
import com.github.kxrxh.javalin.rest.database.models.Transaction;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class ReportService {

    private ReportService() {
    }

    public static Report generateReport(UUID userId, String reportType, String dateRange) throws SQLException {
        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();

        if (opConn.isEmpty()) {
            throw new GettingConnectionException();
        }

        Connection conn = opConn.get();

        Report report = new Report();
        List<Transaction> transactions = new ArrayList<>();

        StringBuilder query = new StringBuilder(
                "SELECT * FROM transactions WHERE account_id IN (SELECT account_id FROM accounts WHERE user_id = ?)");
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (dateRange != null) {
            query.append(" AND date BETWEEN ? AND ?");
            String[] dates = dateRange.split("to");
            params.add(Timestamp.valueOf(LocalDateTime.parse(dates[0].trim())));
            params.add(Timestamp.valueOf(LocalDateTime.parse(dates[1].trim())));
        }

        try (PreparedStatement ps = conn.prepareStatement(query.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Transaction transaction = Transaction.builder()
                        .accountId(UUID.fromString(rs.getString("transaction_id")))
                        .amount(rs.getLong("amount"))
                        .categoryId(rs.getString("category_id") != null ? UUID.fromString(rs.getString("category_id"))
                                : null)
                        .currency(rs.getString("currency"))
                        .name(rs.getString("name"))
                        .date(rs.getTimestamp("date").toLocalDateTime())
                        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                        .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                        .lastSyncedAt(rs.getTimestamp("last_synced_at") != null
                                ? rs.getTimestamp("last_synced_at").toLocalDateTime()
                                : null)
                        .notes(rs.getString("notes"))
                        .excluded(rs.getBoolean("excluded")).build();
                transactions.add(transaction);
            }

            report.setTransactions(transactions);
            report.setReportType(reportType);
            report.setDateRange(dateRange);
        } catch (SQLException e) {
            throw new SQLException("Error occurred while generating report", e);
        }

        // Insert the generated report into the reports table
        String insertQuery = "INSERT INTO reports (user_id, report_type, date_range) VALUES (?, ?, ?) RETURNING report_id";
        try (PreparedStatement ps2 = conn.prepareStatement(insertQuery)) {
            ps2.setObject(1, userId, java.sql.Types.OTHER);
            ps2.setString(2, reportType);
            ps2.setString(3, dateRange);
            ResultSet rs = ps2.executeQuery();

            if (rs.next()) {
                report.setReportId(UUID.fromString(rs.getString("report_id")));
            }
        } catch (SQLException e) {
            throw new SQLException("Failed to insert report into database", e);
        }

        return report;
    }

    public static Report getReport(UUID reportId) throws SQLException {
        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();
        if (opConn.isEmpty()) {
            throw new GettingConnectionException();
        }

        Connection conn = opConn.get();
        Report report = fetchReport(conn, reportId);
        if (report != null) {
            List<Transaction> transactions = fetchTransactions(conn, report.getUserId(), report.getDateRange());
            report.setTransactions(transactions);
        }
        return report;
    }

    public static Map<String, Object> generateMonthlyReport(int year, int month) throws SQLException {
        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();

        if (opConn.isEmpty()) {
            throw new GettingConnectionException();
        }

        Connection conn = opConn.get();
        Map<String, Object> reportData = new HashMap<>();

        // 1. Fetch total transactions, expenses, and income
        Map<String, Long> transactionSummary = fetchTransactionSummary(conn, year, month);
        long totalTransactions = transactionSummary.get("totalTransactions");
        long totalExpenses = transactionSummary.get("totalExpenses");
        long totalIncome = transactionSummary.get("totalIncome");
        long netBalance = totalIncome - totalExpenses;

        // 2. Fetch expenses by category
        Map<String, Long> expensesByCategory = fetchExpensesByCategory(conn, year, month);

        // 3. Calculate trend compared to the previous month
        double trend = calculateTrend(conn, year, month);

        // 4. Generate expense distribution chart
        Map<String, String> expenseDistributionChart = generateExpenseDistributionChart(expensesByCategory,
                totalExpenses);

        // 5. Fetch top 5 largest expenses
        List<Map<String, String>> largestExpenses = fetchLargestExpenses(conn, year, month);

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

    private static Report fetchReport(Connection conn, UUID reportId) throws SQLException {
        String query = "SELECT report_id, user_id, report_type, date_range, created_at, updated_at FROM reports WHERE report_id = ?";

        Report report = null;
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setObject(1, reportId, java.sql.Types.OTHER);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    report = new Report();
                    report.setReportId(UUID.fromString(rs.getString("report_id")));
                    report.setUserId(UUID.fromString(rs.getString("user_id")));
                    report.setReportType(rs.getString("report_type"));
                    report.setDateRange(rs.getString("date_range"));
                    report.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    report.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Failed to fetch report from database", e);
        }
        return report;
    }

    private static List<Transaction> fetchTransactions(Connection conn, UUID userId, String dateRange)
            throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String transactionQuery = "SELECT transaction_id, name, date, amount, currency, account_id, category_id, excluded, notes, transaction_type, created_at, updated_at, last_synced_at FROM transactions WHERE account_id IN (SELECT account_id FROM accounts WHERE user_id = ?) AND date BETWEEN ? AND ?";
        try (PreparedStatement transactionPs = conn.prepareStatement(transactionQuery)) {
            transactionPs.setObject(1, userId, java.sql.Types.OTHER);
            String[] dates = dateRange.split("to");
            transactionPs.setTimestamp(2, Timestamp.valueOf(LocalDateTime.parse(dates[0].trim())));
            transactionPs.setTimestamp(3, Timestamp.valueOf(LocalDateTime.parse(dates[1].trim())));

            try (ResultSet rs = transactionPs.executeQuery()) {
                while (rs.next()) {
                    Transaction transaction = Transaction.builder()
                            .accountId(UUID.fromString(rs.getString("transaction_id")))
                            .amount(rs.getLong("amount"))
                            .categoryId(
                                    rs.getString("category_id") != null ? UUID.fromString(rs.getString("category_id"))
                                            : null)
                            .currency(rs.getString("currency"))
                            .name(rs.getString("name"))
                            .date(rs.getTimestamp("date").toLocalDateTime())
                            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                            .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                            .lastSyncedAt(rs.getTimestamp("last_synced_at") != null
                                    ? rs.getTimestamp("last_synced_at").toLocalDateTime()
                                    : null)
                            .notes(rs.getString("notes"))
                            .excluded(rs.getBoolean("excluded")).build();
                    transactions.add(transaction);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Failed to fetch transactions from database", e);
        }
        return transactions;
    }

    private static Map<String, Long> fetchTransactionSummary(Connection conn, int year, int month) throws SQLException {
        String query = "SELECT COUNT(*), SUM(CASE WHEN transaction_type = 'INFLOW' THEN amount ELSE 0 END) AS total_inflow, SUM(CASE WHEN transaction_type = 'OUTFLOW' THEN amount ELSE 0 END) AS total_outflow FROM transactions WHERE EXTRACT(YEAR FROM date) = ? AND EXTRACT(MONTH FROM date) = ?";
        Map<String, Long> summary = new HashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, year);
            ps.setInt(2, month);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    summary.put("totalTransactions", rs.getLong(1));
                    summary.put("totalIncome", rs.getLong(2));
                    summary.put("totalExpenses", rs.getLong(3));
                }
            }
        }
        return summary;
    }

    private static Map<String, Long> fetchExpensesByCategory(Connection conn, int year, int month) throws SQLException {
        String query = "SELECT c.name, SUM(t.amount) FROM transactions t INNER JOIN categories c ON t.category_id = c.category_id WHERE EXTRACT(YEAR FROM t.date) = ? AND EXTRACT(MONTH FROM t.date) = ? GROUP BY c.name";
        Map<String, Long> expensesByCategory = new HashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, year);
            ps.setInt(2, month);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    expensesByCategory.put(rs.getString(1), Math.abs(rs.getLong(2)));
                }
            }
        }
        return expensesByCategory;
    }

    private static double calculateTrend(Connection conn, int year, int month) throws SQLException {
        // Compare expenses of the current month with the previous month
        double currentMonthExpenses = fetchTransactionSummary(conn, year, month).get("totalExpenses");
        double previousMonthExpenses = fetchTransactionSummary(conn, year, month - 1).get("totalExpenses");
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

    private static List<Map<String, String>> fetchLargestExpenses(Connection conn, int year, int month)
            throws SQLException {
        String query = "SELECT name AS description, amount FROM transactions WHERE EXTRACT(YEAR FROM date) = ? AND EXTRACT(MONTH FROM date) = ? ORDER BY amount DESC LIMIT 5";
        List<Map<String, String>> largestExpenses = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, year);
            ps.setInt(2, month);
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

    private static String getMonthName(int month) {
        String[] monthNames = { "January", "February", "March", "April", "May", "June", "July", "August", "September",
                "October", "November", "December" };
        return monthNames[(month - 1 + 12) % 12]; // Ensure month wraps around correctly
    }

    // TODO: change USD to local currency
    private static String formatAmount(long amount) {
        return String.format("%s USD", amount < 0 ? "-" + Math.abs(amount) : amount);
    }

    private static Map<String, String> formatExpensesByCategory(Map<String, Long> expensesByCategory) {
        Map<String, String> formattedExpenses = new HashMap<>();
        for (Map.Entry<String, Long> entry : expensesByCategory.entrySet()) {
            formattedExpenses.put(entry.getKey(), formatAmount(entry.getValue()));
        }
        return formattedExpenses;
    }
}
