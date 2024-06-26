package com.github.kxrxh.javalin.rest.services;

import com.github.kxrxh.javalin.rest.database.ConnectionRetrievingException;
import com.github.kxrxh.javalin.rest.database.DatabaseManager;
import com.github.kxrxh.javalin.rest.util.CurrencyConversion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class AccountService extends AbstractService {

    /**
     * Transfers funds from one account to another.
     *
     * @param userId        The UUID of the user initiating the transfer.
     * @param fromAccountId The UUID of the account from which funds will be
     *                      transferred.
     * @param toAccountId   The UUID of the account to which funds will be
     *                      transferred.
     * @param amount        The amount of funds to transfer.
     * @throws SQLException If an SQL exception occurs during the transfer process.
     */
    public static void transferFunds(UUID userId, UUID fromAccountId, UUID toAccountId, long amount)
            throws SQLException {
        if (isUserAuthorized(userId, fromAccountId)) {
            throw new SQLException("User not authorized to transfer from this account");
        }

        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();
        conn.setAutoCommit(false);

        try (PreparedStatement ps1 = conn.prepareStatement(
                "UPDATE accounts SET balance = balance - ? WHERE account_id = ?");
             PreparedStatement ps2 = conn.prepareStatement(
                     "UPDATE accounts SET balance = balance + ? WHERE account_id = ?")) {
            ps1.setLong(1, amount);
            ps1.setObject(2, fromAccountId, java.sql.Types.OTHER);
            int rowsUpdated = ps1.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Source account not found or insufficient funds");
            }

            ps2.setLong(1, amount);
            ps2.setObject(2, toAccountId, java.sql.Types.OTHER);
            rowsUpdated = ps2.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Destination account not found");
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.close();
        }
    }

    /**
     * Merges multiple accounts into a single account.
     *
     * @param userId         The UUID of the user initiating the merge operation.
     * @param accountIds     An array of account IDs to be merged.
     * @param newAccountName The name of the new merged account.
     * @param accountType    The type of the new merged account.
     * @throws SQLException If an SQL exception occurs during the merge process.
     */
    public static void mergeAccounts(UUID userId, String[] accountIds, String newAccountName, String accountType)
            throws SQLException {
        // Check for duplicate account IDs
        Set<String> accountIdSet = new HashSet<>(Arrays.asList(accountIds));
        if (accountIdSet.size() != accountIds.length) {
            throw new SQLException("Duplicate account IDs detected");
        }

        for (String accountIdStr : accountIds) {
            UUID accountId = UUID.fromString(accountIdStr);
            if (isUserAuthorized(userId, accountId)) {
                throw new SQLException("User not authorized to merge this account");
            }
        }

        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new SQLException("Could not get connection");
        }

        Connection conn = optConn.get();
        conn.setAutoCommit(false);
        long totalBalance = 0;
        UUID newAccountId = UUID.randomUUID();

        try {
            for (String accountIdStr : accountIds) {
                UUID accountId = UUID.fromString(accountIdStr);
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT balance, currency FROM accounts WHERE account_id = ?")) {
                    ps.setObject(1, accountId, java.sql.Types.OTHER);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            long balance = rs.getLong("balance");
                            String currency = rs.getString("currency");
                            double convertedBalance = CurrencyConversion.getInstance().convert(balance, currency,
                                    "RUB"); // Convert
                            // all
                            // balances
                            // to RUB
                            totalBalance += (long) convertedBalance;
                        } else {
                            throw new SQLException("Account with ID " + accountId + " not found");
                        }
                    }
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO accounts (account_id, account_name, balance, account_type, user_id) VALUES (?, ?, ?, ?, ?)")) {
                ps.setObject(1, newAccountId, java.sql.Types.OTHER);
                ps.setString(2, newAccountName);
                ps.setLong(3, totalBalance);
                ps.setString(4, accountType);
                ps.setObject(5, userId, java.sql.Types.OTHER);
                ps.executeUpdate();
            }

            try (PreparedStatement psDel = conn.prepareStatement("DELETE FROM accounts WHERE account_id = ?")) {
                for (String accountIdStr : accountIds) {
                    UUID accountId = UUID.fromString(accountIdStr);
                    psDel.setObject(1, accountId, java.sql.Types.OTHER);
                    psDel.addBatch();
                }
                psDel.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new SQLException("Could not rollback transaction", ex);
            }
            throw e;
        } finally {
            conn.close();
        }
    }

    /**
     * Checks if a user is authorized to perform operations on a specific account.
     *
     * @param userId    The UUID of the user to check authorization for.
     * @param accountId The UUID of the account to check authorization for.
     * @return True if the user is authorized to perform operations on the account,
     * false otherwise.
     * @throws SQLException If an SQL exception occurs while checking authorization.
     */
    private static boolean isUserAuthorized(UUID userId, UUID accountId) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT account_id FROM accounts WHERE account_id = ? AND user_id = ?")) {
            ps.setObject(1, accountId, java.sql.Types.OTHER);
            ps.setObject(2, userId, java.sql.Types.OTHER);
            try (ResultSet rs = ps.executeQuery()) {
                return !rs.next();
            }
        } finally {
            conn.close();
        }
    }

}
