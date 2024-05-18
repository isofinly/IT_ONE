package com.github.kxrxh.javalin.rest.services;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import com.github.kxrxh.javalin.rest.database.ConnectionRetrievingException;
import com.github.kxrxh.javalin.rest.database.DatabaseManager;
import com.github.kxrxh.javalin.rest.database.models.AccountDepository;

public class AccountDepositoriesService {

    private AccountDepositoriesService() {
    }

    public static void createDepository(UUID userId, UUID accountId, String bankName, String accountNumber,
            String routingNumber, double interestRate, long overdraftLimit) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO account_depositories (id, account_id, user_id, bank_name, account_number, routing_number, interest_rate, overdraft_limit, created_at, updated_at) "
                        +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")) {
            ps.setObject(1, UUID.randomUUID(), java.sql.Types.OTHER);
            ps.setObject(2, accountId, java.sql.Types.OTHER);
            ps.setObject(3, userId, java.sql.Types.OTHER);
            ps.setString(4, bankName);
            ps.setString(5, accountNumber);
            ps.setString(6, routingNumber);
            ps.setDouble(7, interestRate);
            ps.setLong(8, overdraftLimit);
            ps.executeUpdate();
        }
    }

    public static AccountDepository readDepository(UUID userId, UUID depositoryId) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM account_depositories WHERE id = ? AND user_id = ?")) {
            ps.setObject(1, depositoryId, java.sql.Types.OTHER);
            ps.setObject(2, userId, java.sql.Types.OTHER);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return AccountDepository.builder()
                            .id(UUID.fromString(rs.getString("id")))
                            .accountId(UUID.fromString(rs.getString("account_id")))
                            .userId(UUID.fromString(rs.getString("user_id")))
                            .bankName(rs.getString("bank_name"))
                            .accountNumber(rs.getString("account_number"))
                            .routingNumber(rs.getString("routing_number"))
                            .interestRate(BigDecimal.valueOf(rs.getDouble("interest_rate")))
                            .overdraftLimit(rs.getLong("overdraft_limit"))
                            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                            .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                            .build();
                } else {
                    throw new SQLException("Depository not found");
                }
            }
        }
    }

    public static void updateDepository(UUID userId, UUID depositoryId, UUID accountId, String bankName,
            String accountNumber, String routingNumber, double interestRate, long overdraftLimit) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE account_depositories SET account_id = ?, bank_name = ?, account_number = ?, routing_number = ?, interest_rate = ?, overdraft_limit = ?, updated_at = CURRENT_TIMESTAMP "
                        +
                        "WHERE id = ? AND user_id = ?")) {
            ps.setObject(1, accountId, java.sql.Types.OTHER);
            ps.setString(2, bankName);
            ps.setString(3, accountNumber);
            ps.setString(4, routingNumber);
            ps.setDouble(5, interestRate);
            ps.setLong(6, overdraftLimit);
            ps.setObject(7, depositoryId, java.sql.Types.OTHER);
            ps.setObject(8, userId, java.sql.Types.OTHER);
            ps.executeUpdate();
        }
    }

    public static void deleteDepository(UUID userId, UUID depositoryId) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM account_depositories WHERE id = ? AND user_id = ?")) {
            ps.setObject(1, depositoryId, java.sql.Types.OTHER);
            ps.setObject(2, userId, java.sql.Types.OTHER);
            ps.executeUpdate();
        }
    }

    public static void calculateInterest(UUID userId, UUID depositoryId) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT interest_rate, overdraft_limit FROM account_depositories WHERE id = ? AND user_id = ?")) {
            ps.setObject(1, depositoryId, java.sql.Types.OTHER);
            ps.setObject(2, userId, java.sql.Types.OTHER);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double interestRate = rs.getDouble("interest_rate");
                    long overdraftLimit = rs.getLong("overdraft_limit");
                    double interest = overdraftLimit * (interestRate / 100);

                    // Update the overdraft limit with the calculated interest
                    long newOverdraftLimit = overdraftLimit + (long) interest;

                    try (PreparedStatement psUpdate = conn.prepareStatement(
                            "UPDATE account_depositories SET overdraft_limit = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ? AND user_id = ?")) {
                        psUpdate.setLong(1, newOverdraftLimit);
                        psUpdate.setObject(2, depositoryId, java.sql.Types.OTHER);
                        psUpdate.setObject(3, userId, java.sql.Types.OTHER);
                        psUpdate.executeUpdate();
                    }
                } else {
                    throw new SQLException("Depository not found");
                }
            }
        }
    }
}
