package com.github.kxrxh.javalin.rest.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.github.kxrxh.javalin.rest.database.ConnectionRetrievingException;
import com.github.kxrxh.javalin.rest.database.DatabaseManager;
import com.github.kxrxh.javalin.rest.entities.MockBankIntegration;
import com.github.kxrxh.javalin.rest.interfaces.BankIntegration;

public class IntegrationService extends AbstractService {

    private static final Map<String, BankIntegration> integrations = new HashMap<>();

    static {
        integrations.put("mockbank", new MockBankIntegration());
        // Add other bank integrations here
    }

    private IntegrationService() {
    }

    /**
     * Integrates the user account with a bank.
     *
     * @param userId          The ID of the user.
     * @param bankName        The name of the bank.
     * @param bankCredentials The credentials for accessing the bank.
     * @throws Exception If no integration is found for the specified bank.
     */
    public static void integrateWithBank(UUID userId, String bankName, String bankCredentials) throws Exception {
        BankIntegration integration = integrations.get(bankName.toLowerCase());
        if (integration != null) {
            integration.integrateWithBank(userId, bankCredentials);
        } else {
            throw new Exception("No integration found for bank: " + bankName);
        }
    }

    /**
     * Automatically categorizes transactions for a user based on predefined
     * category mappings.
     *
     * @param userId The ID of the user.
     * @throws SQLException If an SQL error occurs.
     */
    public static void autoCategorizeTransactions(UUID userId) throws SQLException {

        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();
        if (opConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = opConn.get();
        Map<String, UUID> categoryMapping = loadCategoryMappings(userId, conn);

        String query = "SELECT transaction_id, name AS description FROM transactions WHERE account_id IN (SELECT account_id FROM accounts WHERE user_id = ?) AND category_id IS NULL";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setObject(1, userId, java.sql.Types.OTHER);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UUID transactionId = UUID.fromString(rs.getString("transaction_id"));
                String description = rs.getString("description");

                UUID categoryId = categorizeTransaction(description, categoryMapping);
                if (categoryId != null) {
                    updateTransactionCategory(transactionId, categoryId, conn);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error while auto categorizing transactions", e);
        } finally {
            conn.close();
        }
    }

    /**
     * Loads category mappings for a given user from the database.
     *
     * @param userId The ID of the user.
     * @param conn   The database connection.
     * @return A map containing category names as keys and their corresponding UUIDs
     *         as values.
     * @throws SQLException If an SQL error occurs.
     */
    private static Map<String, UUID> loadCategoryMappings(UUID userId, Connection conn) throws SQLException {
        Map<String, UUID> categoryMapping = new HashMap<>();
        String query = "SELECT name, category_id FROM categories WHERE family_id = (SELECT family_id FROM users WHERE user_id = ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setObject(1, userId, java.sql.Types.OTHER);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                categoryMapping.put(rs.getString("name").toLowerCase(), UUID.fromString(rs.getString("category_id")));
            }
        } catch (SQLException e) {
            throw new SQLException("Error while loading category mappings", e);
        } finally {
            conn.close();
        }
        return categoryMapping;
    }

    /**
     * Categorizes a transaction based on its description using the provided
     * category mapping.
     *
     * @param description     The description of the transaction.
     * @param categoryMapping A map containing category names as keys and their
     *                        corresponding UUIDs as values.
     * @return The UUID of the category if a match is found, otherwise null.
     */
    private static UUID categorizeTransaction(String description, Map<String, UUID> categoryMapping) {
        for (Map.Entry<String, UUID> entry : categoryMapping.entrySet()) {
            if (description.toLowerCase().contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Updates the category of a transaction in the database.
     *
     * @param transactionId The ID of the transaction.
     * @param categoryId    The ID of the category to update.
     * @param conn          The database connection.
     * @throws SQLException If an SQL error occurs.
     */
    private static void updateTransactionCategory(UUID transactionId, UUID categoryId, Connection conn)
            throws SQLException {
        String query = "UPDATE transactions SET category_id = ? WHERE transaction_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setObject(1, categoryId, java.sql.Types.OTHER);
            ps.setObject(2, transactionId, java.sql.Types.OTHER);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error while updating transaction category", e);
        } finally {
            conn.close();
        }
    }
}
