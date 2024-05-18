package com.github.kxrxh.javalin.rest.services;

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
import com.github.kxrxh.javalin.rest.database.models.AccountCredit;

public class AccountCreditsService {

    private AccountCreditsService() {
    }

    public static void createCredit(UUID userId, UUID accountId, long creditLimit, double interestRate,
            LocalDate dueDate, long minimumPayment) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO account_credits (id, account_id, user_id, credit_limit, interest_rate, due_date, minimum_payment, created_at, updated_at) "
                        +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")) {
            ps.setObject(1, UUID.randomUUID(), java.sql.Types.OTHER);
            ps.setObject(2, accountId, java.sql.Types.OTHER);
            ps.setObject(3, userId, java.sql.Types.OTHER);
            ps.setLong(4, creditLimit);
            ps.setDouble(5, interestRate);
            ps.setDate(6, Date.valueOf(dueDate));
            ps.setLong(7, minimumPayment);
            ps.executeUpdate();
        }
    }

    public static AccountCredit readCredit(UUID userId, UUID creditId) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM account_credits WHERE id = ? AND user_id = ?")) {
            ps.setObject(1, creditId, java.sql.Types.OTHER);
            ps.setObject(2, userId, java.sql.Types.OTHER);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return AccountCredit.builder()
                            .id(UUID.fromString(rs.getString("id")))
                            .accountId(UUID.fromString(rs.getString("account_id")))
                            .userId(UUID.fromString(rs.getString("user_id")))
                            .creditLimit(rs.getLong("credit_limit"))
                            .interestRate(rs.getBigDecimal("interest_rate"))
                            .dueDate(rs.getDate("due_date").toLocalDate())
                            .minimumPayment(rs.getLong("minimum_payment"))
                            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                            .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                            .build();
                } else {
                    throw new SQLException("Credit not found");
                }
            }
        }
    }

    public static void updateCredit(UUID userId, UUID creditId, UUID accountId, long creditLimit, double interestRate,
            LocalDate dueDate, long minimumPayment) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE account_credits SET account_id = ?, credit_limit = ?, interest_rate = ?, due_date = ?, minimum_payment = ?, updated_at = CURRENT_TIMESTAMP "
                        +
                        "WHERE id = ? AND user_id = ?")) {
            ps.setObject(1, accountId, java.sql.Types.OTHER);
            ps.setLong(2, creditLimit);
            ps.setDouble(3, interestRate);
            ps.setDate(4, Date.valueOf(dueDate));
            ps.setLong(5, minimumPayment);
            ps.setObject(6, creditId, java.sql.Types.OTHER);
            ps.setObject(7, userId, java.sql.Types.OTHER);
            ps.executeUpdate();
        }
    }

    public static void deleteCredit(UUID userId, UUID creditId) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM account_credits WHERE id = ? AND user_id = ?")) {
            ps.setObject(1, creditId, java.sql.Types.OTHER);
            ps.setObject(2, userId, java.sql.Types.OTHER);
            ps.executeUpdate();
        }
    }

    public static void calculateInterest(UUID userId, UUID creditId) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT credit_limit, interest_rate FROM account_credits WHERE id = ? AND user_id = ?")) {
            ps.setObject(1, creditId, java.sql.Types.OTHER);
            ps.setObject(2, userId, java.sql.Types.OTHER);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long creditLimit = rs.getLong("credit_limit");
                    double interestRate = rs.getDouble("interest_rate");
                    double interest = creditLimit * (interestRate / 100);

                    // Update the credit limit with the calculated interest
                    long newCreditLimit = creditLimit + (long) interest;

                    try (PreparedStatement psUpdate = conn.prepareStatement(
                            "UPDATE account_credits SET credit_limit = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ? AND user_id = ?")) {
                        psUpdate.setLong(1, newCreditLimit);
                        psUpdate.setObject(2, creditId, java.sql.Types.OTHER);
                        psUpdate.setObject(3, userId, java.sql.Types.OTHER);
                        psUpdate.executeUpdate();
                    }
                } else {
                    throw new SQLException("Credit not found");
                }
            }
        }
    }
}
