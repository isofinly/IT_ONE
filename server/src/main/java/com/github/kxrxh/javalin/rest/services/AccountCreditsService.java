package com.github.kxrxh.javalin.rest.services;

import com.github.kxrxh.javalin.rest.database.ConnectionRetrievingException;
import com.github.kxrxh.javalin.rest.database.DatabaseManager;
import com.github.kxrxh.javalin.rest.database.models.AccountCredit;
import com.github.kxrxh.javalin.rest.util.NATSUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AccountCreditsService extends AbstractService {

    private AccountCreditsService() {
    }

    public static void createCredit(UUID accountId, long creditLimit, double interestRate,
                                    LocalDate dueDate, long minimumPayment) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO account_credits (id, account_id, credit_limit, interest_rate, due_date, minimum_payment, created_at, updated_at) "
                        + "VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")) {
            ps.setObject(1, UUID.randomUUID(), java.sql.Types.OTHER);
            ps.setObject(2, accountId, java.sql.Types.OTHER);
            ps.setLong(3, creditLimit);
            ps.setDouble(4, interestRate);
            ps.setDate(5, Date.valueOf(dueDate));
            ps.setLong(6, minimumPayment);
            ps.executeUpdate();
        } finally {
            conn.close();
        }
    }

    public static UUID createAccount(UUID userId, String accountType, String accountName) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();
        UUID accountId = UUID.randomUUID();
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO accounts (account_id, user_id, account_type, account_name, created_at, updated_at, is_active, status) "
                        + "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 'ok')")) {
            ps.setObject(1, accountId, java.sql.Types.OTHER);
            ps.setObject(2, userId, java.sql.Types.OTHER);
            ps.setString(3, accountType);
            ps.setString(4, accountName);
            ps.executeUpdate();
        } finally {
            conn.close();
        }
        return accountId;
    }

    public static AccountCredit[] readCredit(UUID userId) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();
        List<AccountCredit> accountCredits = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id, account_id, credit_limit, interest_rate, due_date, minimum_payment, created_at, updated_at FROM account_credits WHERE account_id IN (SELECT account_id FROM accounts WHERE user_id = ?)")) {

            ps.setObject(1, userId, java.sql.Types.OTHER);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AccountCredit accountCredit = AccountCredit.builder()
                            .id(UUID.fromString(rs.getString("id")))
                            .accountId(UUID.fromString(rs.getString("account_id")))
                            .creditLimit(rs.getLong("credit_limit"))
                            .interestRate(rs.getDouble("interest_rate"))
                            .dueDate(rs.getDate(DUE_DATE) != null ? rs.getDate(DUE_DATE).toLocalDate() : null)
                            .minimumPayment(rs.getLong("minimum_payment"))
                            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                            .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                            .build();
                    accountCredits.add(accountCredit);
                }
            }
        } finally {
            conn.close();
        }
        return accountCredits.toArray(new AccountCredit[0]);
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
                        + "WHERE id = ? AND account_id IN (SELECT account_id FROM accounts WHERE user_id = ?)")) {
            ps.setObject(1, accountId, java.sql.Types.OTHER);
            ps.setLong(2, creditLimit);
            ps.setDouble(3, interestRate);
            ps.setDate(4, Date.valueOf(dueDate));
            ps.setLong(5, minimumPayment);
            ps.setObject(6, creditId, java.sql.Types.OTHER);
            ps.setObject(7, userId, java.sql.Types.OTHER);
            ps.executeUpdate();
        } finally {
            conn.close();
        }
    }

    public static void deleteCredit(UUID userId, UUID creditId) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM account_credits WHERE id = ? AND account_id IN (SELECT account_id FROM accounts WHERE user_id = ?)")) {
            ps.setObject(1, creditId, java.sql.Types.OTHER);
            ps.setObject(2, userId, java.sql.Types.OTHER);
            ps.executeUpdate();
        } finally {
            conn.close();
        }
    }

    public static void calculateInterest(UUID userId, UUID creditId) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT ac.credit_limit, ac.interest_rate FROM account_credits ac " +
                        "JOIN accounts a ON ac.account_id = a.account_id " +
                        "WHERE ac.id = ? AND a.user_id = ?")) {
            ps.setObject(1, creditId, java.sql.Types.OTHER);
            ps.setObject(2, userId, java.sql.Types.OTHER);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long creditLimit = rs.getLong("credit_limit");
                    double interestRate = rs.getDouble("interest_rate");
                    double interest = creditLimit * (interestRate / 100);

                    long newCreditLimit = creditLimit + (long) interest;

                    try (PreparedStatement psUpdate = conn.prepareStatement(
                            "UPDATE account_credits SET credit_limit = ?, updated_at = CURRENT_TIMESTAMP " +
                                    "WHERE id = ? AND account_id IN (SELECT account_id FROM accounts WHERE user_id = ?)")) {
                        psUpdate.setLong(1, newCreditLimit);
                        psUpdate.setObject(2, creditId, java.sql.Types.OTHER);
                        psUpdate.setObject(3, userId, java.sql.Types.OTHER);
                        psUpdate.executeUpdate();
                    }
                    checkCreditNotifications(userId, newCreditLimit);

                } else {
                    throw new SQLException("Credit not found");
                }
            }
        } finally {
            conn.close();
        }
    }

    public static void checkDueDateNotifications(UUID userId) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        String query = "SELECT ac.id, ac.due_date FROM account_credits ac " +
                "JOIN accounts a ON ac.account_id = a.account_id " +
                "WHERE a.user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setObject(1, userId, java.sql.Types.OTHER);
            try (ResultSet rs = ps.executeQuery()) {
                LocalDate today = LocalDate.now();
                while (rs.next()) {
                    LocalDate dueDate = rs.getDate("due_date").toLocalDate();
                    Period period = Period.between(today, dueDate);
                    int daysUntilDue = period.getDays();

                    if (daysUntilDue <= 7) {
                        NATSUtil.publish(userId.toString(), "Your credit payment is due in " + daysUntilDue + " days.");
                    }
                }
            }
        } finally {
            conn.close();
        }
    }

    private static void checkCreditNotifications(UUID userId, long creditLimit) {
        long threshold10 = (long) (creditLimit * 0.1);
        long threshold5 = (long) (creditLimit * 0.05);

        if (creditLimit <= threshold10) {
            NATSUtil.publish(userId.toString(), "You are within 10% of your credit limit.");
        }
        if (creditLimit <= threshold5) {
            NATSUtil.publish(userId.toString(), "You are within 5% of your credit limit.");
        }
        if (creditLimit == 0) {
            NATSUtil.publish(userId.toString(), "You have reached your credit limit.");
        }
    }
}
