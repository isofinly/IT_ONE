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
import com.github.kxrxh.javalin.rest.database.models.AccountBalance;

public class AccountBalancesService {

    private AccountBalancesService() {
    }

    public static void createBalance(UUID userId, UUID accountId, LocalDate date, long balance, String currency)
            throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO account_balances (id, account_id, user_id, date, balance, currency, created_at, updated_at) "
                        +
                        "VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")) {
            ps.setObject(1, UUID.randomUUID(), java.sql.Types.OTHER);
            ps.setObject(2, accountId, java.sql.Types.OTHER);
            ps.setObject(3, userId, java.sql.Types.OTHER);
            ps.setDate(4, Date.valueOf(date));
            ps.setLong(5, balance);
            ps.setString(6, currency);
            ps.executeUpdate();
        }
    }

    public static AccountBalance readBalance(UUID userId, UUID balanceId) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM account_balances WHERE id = ? AND user_id = ?")) {
            ps.setObject(1, balanceId, java.sql.Types.OTHER);
            ps.setObject(2, userId, java.sql.Types.OTHER);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return AccountBalance.builder()
                            .id(UUID.fromString(rs.getString("id")))
                            .accountId(UUID.fromString(rs.getString("account_id")))
                            .accountId(UUID.fromString(rs.getString("user_id")))
                            .date(rs.getDate("date").toLocalDate())
                            .balance(rs.getLong("balance"))
                            .currency(rs.getString("currency"))
                            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                            .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                            .build();
                } else {
                    throw new SQLException("Balance not found");
                }
            }
        }
    }

    public static void updateBalance(UUID userId, UUID balanceId, UUID accountId, LocalDate date, long balance,
            String currency) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE account_balances SET account_id = ?, date = ?, balance = ?, currency = ?, updated_at = CURRENT_TIMESTAMP "
                        +
                        "WHERE id = ? AND user_id = ?")) {
            ps.setObject(1, accountId, java.sql.Types.OTHER);
            ps.setDate(2, Date.valueOf(date));
            ps.setLong(3, balance);
            ps.setString(4, currency);
            ps.setObject(5, balanceId, java.sql.Types.OTHER);
            ps.setObject(6, userId, java.sql.Types.OTHER);
            ps.executeUpdate();
        }
    }

    public static void deleteBalance(UUID userId, UUID balanceId) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM account_balances WHERE id = ? AND user_id = ?")) {
            ps.setObject(1, balanceId, java.sql.Types.OTHER);
            ps.setObject(2, userId, java.sql.Types.OTHER);
            ps.executeUpdate();
        }
    }

    public static long calculateTotalBalance(UUID userId, UUID accountId) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT SUM(balance) as total_balance FROM account_balances WHERE user_id = ? AND account_id = ?")) {
            ps.setObject(1, userId, java.sql.Types.OTHER);
            ps.setObject(2, accountId, java.sql.Types.OTHER);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("total_balance");
                } else {
                    throw new SQLException("Balances not found for the specified user and account");
                }
            }
        }
    }
}
