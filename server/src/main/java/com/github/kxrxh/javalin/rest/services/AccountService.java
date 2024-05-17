package com.github.kxrxh.javalin.rest.services;

import com.github.kxrxh.javalin.rest.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountService {

    public void transferFunds(long fromAccountId, long toAccountId, long amount) throws SQLException {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Subtract amount from source account
                try (PreparedStatement ps1 = conn.prepareStatement("UPDATE accounts SET balance = balance - ? WHERE account_id = ?");
                     PreparedStatement ps2 = conn.prepareStatement("UPDATE accounts SET balance = balance + ? WHERE account_id = ?")) {
                    ps1.setLong(1, amount);
                    ps1.setLong(2, fromAccountId);
                    int rowsUpdated = ps1.executeUpdate();
                    if (rowsUpdated == 0) {
                        conn.rollback();
                        throw new SQLException("Source account not found or insufficient funds");
                    }

                    // Add amount to destination account
                    ps2.setLong(1, amount);
                    ps2.setLong(2, toAccountId);
                    rowsUpdated = ps2.executeUpdate();
                    if (rowsUpdated == 0) {
                        conn.rollback();
                        throw new SQLException("Destination account not found");
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public void mergeAccounts(String[] accountIds, String newAccountName) throws SQLException {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                long totalBalance = 0;

                // Calculate total balance from all accounts
                for (String accountIdStr : accountIds) {
                    long accountId = Long.parseLong(accountIdStr);
                    PreparedStatement ps = conn.prepareStatement("SELECT balance FROM accounts WHERE account_id = ?");
                    ps.setLong(1, accountId);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        totalBalance += rs.getLong("balance");
                    } else {
                        conn.rollback();
                        throw new SQLException("Account with ID " + accountId + " not found");
                    }
                }

                // Create new account with total balance
                PreparedStatement ps = conn.prepareStatement("INSERT INTO accounts (account_name, balance) VALUES (?, ?)");
                ps.setString(1, newAccountName);
                ps.setLong(2, totalBalance);
                ps.executeUpdate();

                // Delete old accounts
                for (String accountIdStr : accountIds) {
                    long accountId = Long.parseLong(accountIdStr);
                    PreparedStatement psDel = conn.prepareStatement("DELETE FROM accounts WHERE account_id = ?");
                    psDel.setLong(1, accountId);
                    psDel.executeUpdate();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}
