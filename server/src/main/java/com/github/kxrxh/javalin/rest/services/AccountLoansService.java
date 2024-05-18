package com.github.kxrxh.javalin.rest.services;

import com.github.kxrxh.javalin.rest.database.ConnectionRetrievingException;
import com.github.kxrxh.javalin.rest.database.DatabaseManager;
import com.github.kxrxh.javalin.rest.database.models.AccountLoan;
import com.github.kxrxh.javalin.rest.util.NATSUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;
import java.util.UUID;

public class AccountLoansService extends AbstractService {

    /**
     * Creates a new loan associated with a user account.
     *
     * @param accountId          The UUID of the account to which the loan will be
     *                           associated.
     * @param loanAmount         The amount of the loan.
     * @param outstandingBalance The outstanding balance of the loan.
     * @param interestRate       The interest rate of the loan.
     * @param loanTerm           The term of the loan.
     * @param dueDate            The due date of the loan.
     * @param paymentFrequency   The payment frequency of the loan.
     * @param collateral         The collateral for the loan.
     * @throws SQLException If an SQL exception occurs during the loan creation
     *                      process.
     */
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

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT EXISTS (SELECT 1 FROM accounts WHERE user_id = ? AND account_id = ?)")) {
            ps.setObject(1, userId, java.sql.Types.OTHER);
            ps.setObject(2, accountId, java.sql.Types.OTHER);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next() && rs.getBoolean(1)) {
                    throw new IllegalArgumentException("No result found.");
                }
            }
        } finally {
            conn.close();
        }

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO account_loans (id, account_id, loan_amount, outstanding_balance, interest_rate, loan_term, due_date, payment_frequency, collateral, created_at, updated_at) "
                        +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")) {
            ps.setObject(1, UUID.randomUUID(), java.sql.Types.OTHER);
            ps.setObject(2, accountId, java.sql.Types.OTHER);
            ps.setLong(3, loanAmount);
            ps.setLong(4, outstandingBalance);
            ps.setDouble(5, interestRate);
            ps.setString(6, loanTerm);
            ps.setDate(7, Date.valueOf(dueDate));
            ps.setString(8, paymentFrequency);
            ps.setString(9, collateral);
            ps.executeUpdate();
        } finally {
            conn.close();
        }
    }

    /**
     * Retrieves information about a specific loan associated with a user account.
     *
     * @param userId The UUID of the user who owns the account.
     * @param loanId The UUID of the loan to retrieve.
     * @return An AccountLoan object representing the retrieved loan.
     * @throws SQLException If an SQL exception occurs during the loan retrieval
     *                      process.
     */
    public static AccountLoan readLoan(UUID userId, UUID loanId)
            throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT al.* FROM account_loans al " +
                        ON_AL_ACCOUNT_ID_A_ACCOUNT_ID +
                        "WHERE al.id = ? AND a.user_id = ?")) {
            ps.setObject(1, loanId, java.sql.Types.OTHER);
            ps.setObject(2, userId, java.sql.Types.OTHER);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return AccountLoan.builder()
                            .id(UUID.fromString(rs.getString("id")))
                            .accountId(UUID.fromString(rs.getString("account_id")))
                            .loanAmount(rs.getLong("loan_amount"))
                            .outstandingBalance(rs.getLong("outstanding_balance"))
                            .interestRate(rs.getDouble("interest_rate"))
                            .loanTerm(rs.getString("loan_term"))
                            .dueDate(rs.getDate("due_date").toLocalDate())
                            .paymentFrequency(rs.getString("payment_frequency"))
                            .collateral(rs.getString("collateral"))
                            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                            .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                            .build();
                } else {
                    throw new SQLException("Loan not found");
                }
            }
        } finally {
            conn.close();
        }
    }

    /**
     * Updates information about a specific loan associated with a user account.
     *
     * @param userId             The UUID of the user who owns the account.
     * @param loanId             The UUID of the loan to update.
     * @param accountId          The UUID of the account to which the loan is
     *                           associated.
     * @param loanAmount         The updated amount of the loan.
     * @param outstandingBalance The updated outstanding balance of the loan.
     * @param interestRate       The updated interest rate of the loan.
     * @param loanTerm           The updated term of the loan.
     * @param dueDate            The updated due date of the loan.
     * @param paymentFrequency   The updated payment frequency of the loan.
     * @param collateral         The updated collateral for the loan.
     * @throws SQLException If an SQL exception occurs during the loan update
     *                      process.
     */
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

        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE account_loans SET account_id = ?, loan_amount = ?, outstanding_balance = ?, interest_rate = ?, loan_term = ?, due_date = ?, payment_frequency = ?, collateral = ?, updated_at = CURRENT_TIMESTAMP "
                        +
                        "WHERE id = ? AND account_id IN (SELECT account_id FROM accounts WHERE user_id = ?)")) {
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
        } finally {
            conn.close();
        }
    }

    /**
     * Deletes a specific loan associated with a user account.
     *
     * @param userId The UUID of the user who owns the account.
     * @param loanId The UUID of the loan to delete.
     * @throws SQLException If an SQL exception occurs during the loan deletion
     *                      process.
     */
    public static void deleteLoan(UUID userId, UUID loanId) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM account_loans WHERE id = ? AND account_id IN (SELECT account_id FROM accounts WHERE user_id = ?)")) {
            ps.setObject(1, loanId, java.sql.Types.OTHER);
            ps.setObject(2, userId, java.sql.Types.OTHER);
            ps.executeUpdate();
        } finally {
            conn.close();
        }
    }

    /**
     * Calculates interest for a specific loan associated with a user account.
     *
     * @param userId The UUID of the user who owns the account.
     * @param loanId The UUID of the loan for which interest will be calculated.
     * @throws SQLException If an SQL exception occurs during the interest
     *                      calculation process.
     */
    public static void calculateInterest(UUID userId, UUID loanId)
            throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT al.outstanding_balance, al.interest_rate FROM account_loans al " +
                        "JOIN accounts a ON al.account_id = a.account_id " +
                        "WHERE al.id = ? AND a.user_id = ?")) {
            ps.setObject(1, loanId, java.sql.Types.OTHER);
            ps.setObject(2, userId, java.sql.Types.OTHER);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long outstandingBalance = rs.getLong("outstanding_balance");
                    double interestRate = rs.getDouble("interest_rate");
                    double interest = outstandingBalance * (interestRate / 100);

                    long newOutstandingBalance = outstandingBalance + (long) interest;

                    try (PreparedStatement psUpdate = conn.prepareStatement(
                            "UPDATE account_loans SET outstanding_balance = ?, updated_at = CURRENT_TIMESTAMP " +
                                    "WHERE id = ? AND account_id IN (SELECT account_id FROM accounts WHERE user_id = ?)")) {
                        psUpdate.setLong(1, newOutstandingBalance);
                        psUpdate.setObject(2, loanId, java.sql.Types.OTHER);
                        psUpdate.setObject(3, userId, java.sql.Types.OTHER);
                        psUpdate.executeUpdate();
                    }

                    checkLoanNotifications(userId, newOutstandingBalance);

                } else {
                    throw new SQLException("Loan not found");
                }
            }
        } finally {
            conn.close();
        }
    }

    /**
     * Checks loan due date notifications for a user.
     *
     * @param userId The UUID of the user to check loan due date notifications for.
     * @throws SQLException If an SQL exception occurs during the due date
     *                      notification process.
     */
    public static void checkDueDateNotifications(UUID userId) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        String query = "SELECT al.id, al.due_date FROM account_loans al " +
                "JOIN accounts a ON al.account_id = a.account_id " +
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
                        NATSUtil.publish(userId.toString(), "Your loan payment is due in " + daysUntilDue + " days.");
                    }
                }
            }
        } finally {
            conn.close();
        }
    }

    /**
     * Checks loan notifications based on outstanding balance for a specific user.
     *
     * @param userId             The UUID of the user to check loan notifications
     *                           for.
     * @param outstandingBalance The outstanding balance of the loan.
     */
    private static void checkLoanNotifications(UUID userId, long outstandingBalance) {
        long threshold10 = (long) (outstandingBalance * 0.1);
        long threshold5 = (long) (outstandingBalance * 0.05);

        if (outstandingBalance <= threshold10) {
            NATSUtil.publish(userId.toString(), "You are within 10% of your loan limit.");
        }
        if (outstandingBalance <= threshold5) {
            NATSUtil.publish(userId.toString(), "You are within 5% of your loan limit.");
        }
        if (outstandingBalance == 0) {
            NATSUtil.publish(userId.toString(), "You have reached your loan limit.");
        }
    }
}
