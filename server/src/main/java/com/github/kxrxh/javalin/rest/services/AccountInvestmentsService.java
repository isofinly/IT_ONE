package com.github.kxrxh.javalin.rest.services;

import com.github.kxrxh.javalin.rest.database.ConnectionRetrievingException;
import com.github.kxrxh.javalin.rest.database.DatabaseManager;
import com.github.kxrxh.javalin.rest.database.models.AccountInvestment;

import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public class AccountInvestmentsService extends AbstractService {

    public static void createInvestment(UUID userId, UUID accountId, String investmentType, long marketValue,
                                        long purchasePrice, LocalDate purchaseDate, long dividends, double interestRate) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO account_investments (id, account_id, investment_type, market_value, purchase_price, purchase_date, dividends, interest_rate, created_at, updated_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")) {
            ps.setObject(1, UUID.randomUUID(), java.sql.Types.OTHER);
            ps.setObject(2, accountId, java.sql.Types.OTHER);
            ps.setString(3, investmentType);
            ps.setLong(4, marketValue);
            ps.setLong(5, purchasePrice);
            ps.setDate(6, Date.valueOf(purchaseDate));
            ps.setLong(7, dividends);
            ps.setDouble(8, interestRate);
            ps.executeUpdate();
        } finally {
            conn.close();
        }
    }

    public static AccountInvestment readInvestment(UUID userId, UUID investmentId) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT ai.* FROM account_investments ai " +
                        "JOIN accounts a ON ai.account_id = a.account_id " +
                        "WHERE ai.id = ? AND a.user_id = ?")) {
            ps.setObject(1, investmentId, java.sql.Types.OTHER);
            ps.setObject(2, userId, java.sql.Types.OTHER);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return AccountInvestment.builder()
                            .id(UUID.fromString(rs.getString("id")))
                            .accountId(UUID.fromString(rs.getString("account_id")))
                            .investmentType(rs.getString("investment_type"))
                            .marketValue(rs.getLong("market_value"))
                            .purchasePrice(rs.getLong("purchase_price"))
                            .purchaseDate(rs.getDate("purchase_date").toLocalDate())
                            .dividends(rs.getLong("dividends"))
                            .interestRate(rs.getLong("interest_rate"))
                            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                            .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                            .build();
                } else {
                    throw new SQLException("Investment not found");
                }
            }
        } finally {
            conn.close();
        }
    }

    public static void updateInvestment(UUID userId, UUID investmentId, UUID accountId, String investmentType,
                                        long marketValue, long purchasePrice, LocalDate purchaseDate, long dividends, double interestRate)
            throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE account_investments SET account_id = ?, investment_type = ?, market_value = ?, purchase_price = ?, purchase_date = ?, dividends = ?, interest_rate = ?, updated_at = CURRENT_TIMESTAMP " +
                        "WHERE id = ? AND account_id IN (SELECT account_id FROM accounts WHERE user_id = ?)")) {
            ps.setObject(1, accountId, java.sql.Types.OTHER);
            ps.setString(2, investmentType);
            ps.setLong(3, marketValue);
            ps.setLong(4, purchasePrice);
            ps.setDate(5, Date.valueOf(purchaseDate));
            ps.setLong(6, dividends);
            ps.setDouble(7, interestRate);
            ps.setObject(8, investmentId, java.sql.Types.OTHER);
            ps.setObject(9, userId, java.sql.Types.OTHER);
            ps.executeUpdate();
        } finally {
            conn.close();
        }
    }

    public static void deleteInvestment(UUID userId, UUID investmentId) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM account_investments WHERE id = ? AND account_id IN (SELECT account_id FROM accounts WHERE user_id = ?)")) {
            ps.setObject(1, investmentId, java.sql.Types.OTHER);
            ps.setObject(2, userId, java.sql.Types.OTHER);
            ps.executeUpdate();
        } finally {
            conn.close();
        }
    }

    public static void calculateDividends(UUID userId, UUID investmentId) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT ai.dividends, ai.interest_rate FROM account_investments ai " +
                        "JOIN accounts a ON ai.account_id = a.account_id " +
                        "WHERE ai.id = ? AND a.user_id = ?")) {
            ps.setObject(1, investmentId, java.sql.Types.OTHER);
            ps.setObject(2, userId, java.sql.Types.OTHER);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long dividends = rs.getLong("dividends");
                    double interestRate = rs.getDouble("interest_rate");
                    double newDividends = dividends * (1 + (interestRate / 100));

                    // Update the dividends with the calculated interest
                    try (PreparedStatement psUpdate = conn.prepareStatement(
                            "UPDATE account_investments SET dividends = ?, updated_at = CURRENT_TIMESTAMP " +
                                    "WHERE id = ? AND account_id IN (SELECT account_id FROM accounts WHERE user_id = ?)")) {
                        psUpdate.setLong(1, (long) newDividends);
                        psUpdate.setObject(2, investmentId, java.sql.Types.OTHER);
                        psUpdate.setObject(3, userId, java.sql.Types.OTHER);
                        psUpdate.executeUpdate();
                    }
                } else {
                    throw new SQLException("Investment not found");
                }
            }
        } finally {
            conn.close();
        }
    }

}
