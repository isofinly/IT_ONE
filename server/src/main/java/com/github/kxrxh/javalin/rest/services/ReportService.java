package com.github.kxrxh.javalin.rest.services;

import com.github.kxrxh.javalin.rest.database.DatabaseManager;
import com.github.kxrxh.javalin.rest.database.models.Report;
import com.github.kxrxh.javalin.rest.database.models.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportService {

    public static Report generateReport(Long userId, String reportType, String dateRange) throws SQLException {
        Report report = new Report();
        List<Transaction> transactions = new ArrayList<>();

        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            StringBuilder query = new StringBuilder("SELECT * FROM transactions WHERE user_id = ?");
            List<Object> params = new ArrayList<>();
            params.add(userId);

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

            report.setTransactions(transactions);
            report.setReportType(reportType);
            report.setDateRange(dateRange);

            // Insert the generated report into the reports table
            String insertQuery = "INSERT INTO reports (user_id, report_type, date_range) VALUES (?, ?, ?)";
            ps = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, userId);
            ps.setString(2, reportType);
            ps.setString(3, dateRange);
            ps.executeUpdate();

            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                report.setReportId(generatedKeys.getLong(1));
            }
        }

        return report;
    }

    public static Report getReport(Long reportId) throws SQLException {
        Report report = null;

        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "SELECT * FROM reports WHERE report_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setLong(1, reportId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                report = new Report();
                report.setReportId(rs.getLong("report_id"));
                report.setUserId(rs.getLong("user_id"));
                report.setReportType(rs.getString("report_type"));
                report.setDateRange(rs.getString("date_range"));

                // Fetch transactions for the report
                List<Transaction> transactions = new ArrayList<>();
                String transactionQuery = "SELECT * FROM transactions WHERE user_id = ? AND transaction_date BETWEEN ? AND ?";
                PreparedStatement transactionPs = conn.prepareStatement(transactionQuery);
                transactionPs.setLong(1, rs.getLong("user_id"));
                String[] dates = rs.getString("date_range").split("to");
                transactionPs.setTimestamp(2, Timestamp.valueOf(dates[0].trim()));
                transactionPs.setTimestamp(3, Timestamp.valueOf(dates[1].trim()));

                ResultSet transactionRs = transactionPs.executeQuery();
                while (transactionRs.next()) {
                    Transaction transaction = new Transaction(
                            transactionRs.getLong("transaction_id"),
                            transactionRs.getLong("amount"),
                            transactionRs.getTimestamp("transaction_date"),
                            transactionRs.getLong("category_id"),
                            transactionRs.getLong("user_id"),
                            transactionRs.getString("description")
                    );
                    transactions.add(transaction);
                }
                report.setTransactions(transactions);
            }
        }

        return report;
    }
}
