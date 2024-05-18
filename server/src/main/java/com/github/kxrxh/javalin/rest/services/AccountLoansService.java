package com.github.kxrxh.javalin.rest.services;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import com.github.kxrxh.javalin.rest.database.ConnectionRetrievingException;
import com.github.kxrxh.javalin.rest.database.DatabaseManager;
import com.github.kxrxh.javalin.rest.database.models.AccountLoan;

public class AccountLoansService {

    private AccountLoansService() {
    }

    public static void createLoan(UUID userId, UUID accountId, long loanAmount,
            long outstandingBalance, double interestRate,
            String loanTerm, LocalDate dueDate,
            String paymentFrequency, String collateral)
            throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO account_loans (id, account_id, user_id, loan_amount, outstanding_balance, interest_rate, loan_term, due_date, payment_frequency, collateral, created_at, updated_at) "
                                +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")) {
            ps.setObject(1, UUID.randomUUID(), java.sql.Types.OTHER);
            ps.setObject(2, accountId, java.sql.Types.OTHER);
            ps.setObject(3, userId, java.sql.Types.OTHER);
            ps.setLong(4, loanAmount);
            ps.setLong(5, outstandingBalance);
            ps.setDouble(6, interestRate);
            ps.setString(7, loanTerm);
            ps.setDate(8, Date.valueOf(dueDate));
            ps.setString(9, paymentFrequency);
            ps.setString(10, collateral);
            ps.executeUpdate();
        }
    }

    public static AccountLoan readLoan(UUID userId, UUID loanId)
            throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM account_loans WHERE id = ? AND user_id = ?")) {
            ps.setObject(1, loanId, java.sql.Types.OTHER);
            ps.setObject(2, userId, java.sql.Types.OTHER);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return AccountLoan.builder()
                            .id(UUID.fromString(rs.getString("id")))
                            .accountId(UUID.fromString(rs.getString("account_id")))
                            .userId(UUID.fromString(rs.getString("user_id")))
                            .loanAmount(rs.getLong("loan_amount"))
                            .outstandingBalance(rs.getLong("outstanding_balance"))
                            .interestRate(BigDecimal.valueOf(rs.getDouble("interest_rate")))
                            .loanTerm(rs.getString("loan_term"))
                            .dueDate(rs.getDate("due_date").toLocalDate())
                            .paymentFrequency(rs.getString("payment_frequency"))
                            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                            .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                            .build();
                } else {
                    throw new SQLException("Loan not found");
                }
            }
        }
    }

    public static void updateLoan(UUID userId, UUID loanId, UUID accountId,
            long loanAmount, long outstandingBalance,
            double interestRate, String loanTerm,
            LocalDate dueDate, String paymentFrequency,
            String collateral) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE account_loans SET account_id = ?, loan_amount = ?, outstanding_balance = ?, interest_rate = ?, loan_term = ?, due_date = ?, payment_frequency = ?, collateral = ?, updated_at = CURRENT_TIMESTAMP "
                                + "WHERE id = ? AND user_id = ?")) {
            ps.setObject(1, accountId, java.sql.Types.OTHER);
            ps.setLong(2, loanAmount);
            ps.setLong(3, outstandingBalance);
            ps.setDouble(4, interestRate);
            ps.setString(5, loanTerm);
            ps.setDate(6, Date.valueOf(dueDate));
            ps.setString(7, paymentFrequency);
            ps.setString(8, collateral);
            ps.setObject(9, loanId, java.sql.Types.OTHER);
            ps.setObject(10, userId, java.sql.Types.OTHER);
            ps.executeUpdate();
        }
    }

    public static void deleteLoan(UUID userId, UUID loanId) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM account_loans WHERE id = ? AND user_id = ?")) {
            ps.setObject(1, loanId, java.sql.Types.OTHER);
            ps.setObject(2, userId, java.sql.Types.OTHER);
            ps.executeUpdate();
        }
    }

    public static void calculateInterest(UUID userId, UUID loanId)
            throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT outstanding_balance, interest_rate FROM account_loans WHERE id = ? AND user_id = ?")) {
            ps.setObject(1, loanId, java.sql.Types.OTHER);
            ps.setObject(2, userId, java.sql.Types.OTHER);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long outstandingBalance = rs.getLong("outstanding_balance");
                    double interestRate = rs.getDouble("interest_rate");
                    double interest = outstandingBalance * (interestRate / 100);

                    // Update the outstanding balance with the calculated interest
                    long newOutstandingBalance = outstandingBalance + (long) interest;

                    try (
                            PreparedStatement psUpdate = conn.prepareStatement(
                                    "UPDATE account_loans SET outstanding_balance = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ? AND user_id = ?")) {
                        psUpdate.setLong(1, newOutstandingBalance);
                        psUpdate.setObject(2, loanId, java.sql.Types.OTHER);
                        psUpdate.setObject(3, userId, java.sql.Types.OTHER);
                        psUpdate.executeUpdate();
                    }
                } else {
                    throw new SQLException("Loan not found");
                }
            }
        }
    }

    public static void checkDueDateNotifications(UUID userId) {
        // Placeholder logic for due date notifications
        // This should be replaced with actual notification logic (e.g., email, SMS)
    }
}
