package com.github.kxrxh.javalin.rest.services;

import com.github.kxrxh.javalin.rest.database.ConnectionRetrievingException;
import com.github.kxrxh.javalin.rest.database.DatabaseManager;
import com.github.kxrxh.javalin.rest.database.models.Category;
import com.github.kxrxh.javalin.rest.database.models.Transaction;
import com.github.kxrxh.javalin.rest.database.models.Transaction.TransactionType;
import com.github.kxrxh.javalin.rest.entities.CategoryAnalysisResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryService extends AbstractService {

    /**
     * Analyzes transactions belonging to the specified category within the given
     * date range.
     *
     * @param userId     The ID of the user performing the analysis.
     * @param categoryId The ID of the category to analyze.
     * @param dateRange  A string representing the date range in the format
     *                   "start_date to end_date".
     * @return A CategoryAnalysisResult object containing the analysis result.
     * @throws SQLException If an SQL error occurs during the analysis process.
     */
    public static CategoryAnalysisResult analyzeCategory(UUID userId, UUID categoryId, String dateRange)
            throws SQLException {
        CategoryAnalysisResult result = new CategoryAnalysisResult();
        List<Transaction> transactions = new ArrayList<>();

        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();
        if (opConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = opConn.get();

        // Fetch the family ID for the given user
        UUID familyId = getFamilyIdForUser(conn, userId);
        if (familyId == null) {
            throw new SQLException(NO_FAMILY_FOUND);
        }

        // Check if the user has access to the specified category
        if (!userHasAccessToCategory(conn, userId, categoryId, familyId)) {
            throw new SQLException("User does not have access to the specified category.");
        }

        // Retrieve all categories for the family
        List<UUID> familyCategoryIds = getFamilyCategoryIds(conn, familyId);

        // Find transactions that belong to the family's categories within the given
        // time interval
        StringBuilder query = new StringBuilder("SELECT * FROM transactions WHERE category_id IN (");
        for (int i = 0; i < familyCategoryIds.size(); i++) {
            query.append("?");
            if (i < familyCategoryIds.size() - 1) {
                query.append(",");
            }
        }
        query.append(")");

        List<Object> params = new ArrayList<>(familyCategoryIds);

        if (dateRange != null) {
            query.append(" AND date BETWEEN ? AND ?");
            String[] dates = dateRange.split("to");
            params.add(Timestamp.valueOf(dates[0].trim()));
            params.add(Timestamp.valueOf(dates[1].trim()));
        }

        try (PreparedStatement ps = conn.prepareStatement(query.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            long totalAmount = 0;
            while (rs.next()) {
                Transaction transaction = Transaction.builder()
                        .transactionId(UUID.fromString(rs.getString("transaction_id")))
                        .name(rs.getString("name"))
                        .date(rs.getTimestamp("date").toLocalDateTime())
                        .amount(rs.getLong("amount"))
                        .currency(rs.getString("currency"))
                        .accountId(UUID.fromString(rs.getString("account_id")))
                        .categoryId(rs.getString(CATEGORY_ID) != null ? UUID.fromString(rs.getString(CATEGORY_ID))
                                : null)
                        .excluded(rs.getBoolean("excluded"))
                        .notes(rs.getString("notes"))
                        .transactionType(TransactionType.valueOf(rs.getString("transaction_type").toUpperCase()))
                        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                        .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                        .lastSyncedAt(rs.getTimestamp("last_synced_at") != null
                                ? rs.getTimestamp("last_synced_at").toLocalDateTime()
                                : null)
                        .build();
                transactions.add(transaction);
                totalAmount += transaction.getAmount();
            }

            // Calculate statistics
            result.setTransactions(transactions);
            result.setTotalAmount(totalAmount);
            // Add more statistics calculation as needed

            return result;
        } catch (SQLException e) {
            throw new SQLException("Could not execute query: " + e.getMessage());
        } finally {
            conn.close();
        }
    }

    /**
     * Retrieves the family ID associated with the specified user.
     *
     * @param conn   The database connection.
     * @param userId The ID of the user.
     * @return The family ID associated with the user, or null if not found.
     * @throws SQLException If an SQL error occurs during the retrieval process.
     */
    private static UUID getFamilyIdForUser(Connection conn, UUID userId) throws SQLException {
        String query = "SELECT family_id FROM users WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setObject(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return UUID.fromString(rs.getString("family_id"));
                } else {
                    return null;
                }
            }
        }
    }

    /**
     * Checks whether the user has access to the specified category within the given
     * family.
     *
     * @param conn       The database connection.
     * @param userId     The ID of the user.
     * @param categoryId The ID of the category.
     * @param familyId   The ID of the family.
     * @return True if the user has access to the category, false otherwise.
     * @throws SQLException If an SQL error occurs during the access check.
     */
    private static boolean userHasAccessToCategory(Connection conn, UUID userId, UUID categoryId, UUID familyId) throws SQLException {
        // Check if the user belongs to the specified family
        String userFamilyQuery = "SELECT COUNT(*) FROM users WHERE user_id = ? AND family_id = ?";
        try (PreparedStatement psUserFamily = conn.prepareStatement(userFamilyQuery)) {
            psUserFamily.setObject(1, userId, java.sql.Types.OTHER);
            psUserFamily.setObject(2, familyId, java.sql.Types.OTHER);
            try (ResultSet rsUserFamily = psUserFamily.executeQuery()) {
                if (rsUserFamily.next() && rsUserFamily.getInt(1) > 0) {
                    // User belongs to the specified family, now check if the category belongs to the same family
                    String categoryFamilyQuery = "SELECT COUNT(*) FROM categories WHERE category_id = ? AND family_id = ?";
                    try (PreparedStatement psCategoryFamily = conn.prepareStatement(categoryFamilyQuery)) {
                        psCategoryFamily.setObject(1, categoryId, java.sql.Types.OTHER);
                        psCategoryFamily.setObject(2, familyId, java.sql.Types.OTHER);
                        try (ResultSet rsCategoryFamily = psCategoryFamily.executeQuery()) {
                            if (rsCategoryFamily.next()) {
                                return rsCategoryFamily.getInt(1) > 0;
                            } else {
                                return false;
                            }
                        }
                    }
                } else {
                    return false;
                }
            }
        }
    }


    /**
     * Retrieves all category IDs associated with the specified family.
     *
     * @param conn     The database connection.
     * @param familyId The ID of the family.
     * @return A list of category IDs associated with the family.
     * @throws SQLException If an SQL error occurs during the retrieval process.
     */
    private static List<UUID> getFamilyCategoryIds(Connection conn, UUID familyId) throws SQLException {
        String query = "SELECT category_id FROM categories WHERE family_id = ?";
        List<UUID> categoryIds = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setObject(1, familyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    categoryIds.add(UUID.fromString(rs.getString(CATEGORY_ID)));
                }
            }
        }
        return categoryIds;
    }

    /**
     * Creates a new category in the database for the specified user's family.
     *
     * @param userId The ID of the user creating the category.
     * @param name   The name of the new category.
     * @return The created Category object.
     * @throws SQLException If an SQL error occurs during category creation.
     */
    public static Category createCategory(UUID userId, String name) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        UUID familyId = getFamilyIdForUser(conn, userId);
        if (familyId == null) {
            throw new SQLException(NO_FAMILY_FOUND);
        }

        UUID categoryId = UUID.randomUUID();

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO categories (category_id, name, family_id, created_at, updated_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")) {
            ps.setObject(1, categoryId, java.sql.Types.OTHER);
            ps.setString(2, name);
            ps.setObject(3, familyId, java.sql.Types.OTHER);
            ps.executeUpdate();
        } finally {
            conn.close();
        }

        return readCategory(userId, categoryId);
    }

    /**
     * Retrieves a category from the database based on the provided category ID and
     * user ID.
     *
     * @param userId     The ID of the user retrieving the category.
     * @param categoryId The ID of the category to retrieve.
     * @return The Category object corresponding to the provided IDs.
     * @throws SQLException If the category is not found or an SQL error occurs
     *                      during retrieval.
     */
    public static Category readCategory(UUID userId, UUID categoryId) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        UUID familyId = getFamilyIdForUser(conn, userId);
        if (familyId == null) {
            throw new SQLException(NO_FAMILY_FOUND);
        }

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT category_id, name, family_id, created_at, updated_at FROM categories WHERE category_id = ? AND family_id = ?")) {
            ps.setObject(1, categoryId, java.sql.Types.OTHER);
            ps.setObject(2, familyId, java.sql.Types.OTHER);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Category.builder()
                            .categoryId(UUID.fromString(rs.getString(CATEGORY_ID)))
                            .name(rs.getString("name"))
                            .familyId(UUID.fromString(rs.getString("family_id")))
                            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                            .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime()).build();

                } else {
                    throw new SQLException("Category not found");
                }
            }
        } finally {
            conn.close();
        }
    }

    /**
     * Updates the name of an existing category in the database.
     *
     * @param userId     The ID of the user updating the category.
     * @param categoryId The ID of the category to update.
     * @param name       The new name for the category.
     * @return The updated Category object.
     * @throws SQLException If the category is not found, or an SQL error occurs
     *                      during the update process.
     */
    public static Category updateCategory(UUID userId, UUID categoryId, String name) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        UUID familyId = getFamilyIdForUser(conn, userId);
        if (familyId == null) {
            throw new SQLException(NO_FAMILY_FOUND);
        }

        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE categories SET name = ?, updated_at = CURRENT_TIMESTAMP WHERE category_id = ? AND family_id = ?")) {
            ps.setString(1, name);
            ps.setObject(2, categoryId, java.sql.Types.OTHER);
            ps.setObject(3, familyId, java.sql.Types.OTHER);
            ps.executeUpdate();
        } finally {
            conn.close();
        }

        return readCategory(userId, categoryId);
    }

    /**
     * Deletes a category from the database.
     *
     * @param userId     The ID of the user deleting the category.
     * @param categoryId The ID of the category to delete.
     * @throws SQLException If the category is not found, or an SQL error occurs
     *                      during deletion.
     */
    public static void deleteCategory(UUID userId, UUID categoryId) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        UUID familyId = getFamilyIdForUser(conn, userId);
        if (familyId == null) {
            throw new SQLException(NO_FAMILY_FOUND);
        }

        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM categories WHERE category_id = ? AND family_id = ?")) {
            ps.setObject(1, categoryId, java.sql.Types.OTHER);
            ps.setObject(2, familyId, java.sql.Types.OTHER);
            ps.executeUpdate();
        } finally {
            conn.close();
        }
    }
}