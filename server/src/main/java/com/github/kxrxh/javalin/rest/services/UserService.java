package com.github.kxrxh.javalin.rest.services;

import com.github.kxrxh.javalin.rest.database.ConnectionRetrievingException;
import com.github.kxrxh.javalin.rest.database.DatabaseManager;
import com.github.kxrxh.javalin.rest.database.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class UserService extends AbstractService {
    private static final String USER_ID = "user_id";
    private static final String EMAIL = "email";
    private static final String PASSWORD_DIGEST = "password_digest";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String FAMILY_ID = "family_id";

    /**
     * Creates a new user with the given email and password.
     *
     * @param userId   The ID of the user to create. If null, a new ID will be
     *                 generated.
     * @param email    The email of the user.
     * @param password The hashed password of the user.
     * @return An optional containing the created user if successful, empty
     * otherwise.
     * @throws SQLException If an SQL error occurs.
     */
    public static Optional<User> createUser(UUID userId, String email, String password) throws SQLException {
        String userSql;
        if (userId == null) {
            userSql = "INSERT INTO users (email, password_digest, family_id) VALUES (?,?,?)";
        } else {
            userSql = "INSERT INTO users (user_id, email, password_digest, family_id) VALUES (?,?,?,?)";
        }

        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();
        if (opConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = opConn.get();
        try {
            // Begin transaction
            conn.setAutoCommit(false);

            // Step 1: Insert a new family record
            String familySql = "INSERT INTO families (name) VALUES (?) RETURNING family_id";
            UUID familyId;
            try (PreparedStatement ps = conn.prepareStatement(familySql)) {
                ps.setString(1, "Default Family Name");
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    familyId = UUID.fromString(rs.getString(FAMILY_ID));
                } else {
                    throw new SQLException("Creating family failed, no ID obtained.");
                }
            }

            // Step 2: Insert user with the new family_id
            try (PreparedStatement ps = conn.prepareStatement(userSql)) {
                int count = 1;
                if (userId != null) {
                    ps.setObject(count++, userId, java.sql.Types.OTHER);
                }
                ps.setString(count++, email);
                ps.setString(count++, password); // Assuming password is already hashed
                ps.setObject(count, familyId, java.sql.Types.OTHER);

                int rowInserted = ps.executeUpdate();

                if (rowInserted != 0) {
                    String selectQuery = "SELECT user_id, email, password_digest, family_id FROM users WHERE email = ? AND password_digest = ?";
                    try (PreparedStatement ps2 = conn.prepareStatement(selectQuery)) {
                        ps2.setString(1, email);
                        ps2.setString(2, password);
                        ResultSet rs = ps2.executeQuery();
                        if (rs.next()) {
                            User user = User.builder()
                                    .userId(UUID.fromString(rs.getString(USER_ID)))
                                    .email(rs.getString(EMAIL))
                                    .passwordDigest(rs.getString(PASSWORD_DIGEST))
                                    .familyId(UUID.fromString(rs.getString(FAMILY_ID)))
                                    .build();
                            rs.close();
                            conn.commit(); // Commit transaction
                            return Optional.of(user);
                        }
                        rs.close();
                    } catch (SQLException e) {
                        throw new SQLException("Could not insert new user", e);
                    }
                }
            }
            conn.rollback(); // Rollback transaction if user insertion failed
        } catch (SQLException e) {
            conn.rollback(); // Rollback transaction if any exception occurs
            throw e;
        } finally {
            conn.close(); // Reset auto-commit mode
        }

        return Optional.empty();
    }

    /**
     * Creates a new user with the given email and password.
     *
     * @param email    The email of the user.
     * @param password The hashed password of the user.
     * @return An optional containing the created user if successful, empty
     * otherwise.
     * @throws SQLException If an SQL error occurs.
     */
    public static Optional<User> createUser(String email, String password) throws SQLException {
        return createUser(null, email, password);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return An optional containing the user if found, empty otherwise.
     * @throws SQLException If an SQL error occurs.
     */
    public static Optional<User> getUser(UUID userId) throws SQLException {

        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();
        if (opConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = opConn.get();

        String sql = "SELECT user_id, email, password_digest, family_id, first_name, last_name FROM users WHERE user_id =?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, userId, java.sql.Types.OTHER);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = User.builder()
                        .userId(UUID.fromString(rs.getString(USER_ID)))
                        .email(rs.getString(EMAIL))
                        .passwordDigest(rs.getString(PASSWORD_DIGEST))
                        .familyId(rs.getString(FAMILY_ID) == null ? null : UUID.fromString(rs.getString(FAMILY_ID)))
                        .firstName(rs.getString(FIRST_NAME))
                        .lastName(rs.getString(LAST_NAME))
                        .build();
                return Optional.of(user);
            }
        } finally {
            conn.close();
        }
        return Optional.empty();
    }

    /**
     * Retrieves a user by their email
     *
     * @param email The email of the user to retrieve.
     * @return An optional containing the user if found, empty otherwise.
     * @throws SQLException If an SQL error occurs.
     */
    public static Optional<User> getUser(String email) throws SQLException {
        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();
        if (opConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = opConn.get();

        String sql = "SELECT user_id, email, password_digest, family_id, first_name, last_name FROM users WHERE email =?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = User.builder()
                        .userId(UUID.fromString(rs.getString(USER_ID)))
                        .email(rs.getString(EMAIL))
                        .passwordDigest(rs.getString(PASSWORD_DIGEST))
                        .familyId(rs.getString(FAMILY_ID) == null ? null : UUID.fromString(rs.getString(FAMILY_ID)))
                        .firstName(rs.getString(FIRST_NAME))
                        .lastName(rs.getString(LAST_NAME))
                        .build();
                rs.close();
                return Optional.of(user);
            }
        } finally {
            conn.close();
        }

        return Optional.empty();
    }

    /**
     * Retrieves a user by their email and password.
     *
     * @param email    The email of the user to retrieve.
     * @param password The hashed password of the user.
     * @return An optional containing the user if found, empty otherwise.
     * @throws SQLException If an SQL error occurs.
     */
    public static Optional<User> getUser(String email, String password) throws SQLException {
        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();
        if (opConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = opConn.get();

        String sql = "SELECT user_id, email, password_digest, family_id, first_name, last_name FROM users WHERE email =? AND password_digest =?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password); // Assuming password is already hashed
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = User.builder()
                        .userId(UUID.fromString(rs.getString(USER_ID)))
                        .email(rs.getString(EMAIL))
                        .passwordDigest(rs.getString(PASSWORD_DIGEST))
                        .familyId(rs.getString(FAMILY_ID) == null ? null : UUID.fromString(rs.getString(FAMILY_ID)))
                        .firstName(rs.getString(FIRST_NAME))
                        .lastName(rs.getString(LAST_NAME))
                        .build();
                rs.close();
                return Optional.of(user);
            }
            rs.close();
        } finally {
            conn.close();
        }

        return Optional.empty();
    }

    /**
     * Deletes a user by their ID.
     *
     * @param userId The ID of the user to delete.
     * @return True if the user was successfully deleted, false otherwise.
     * @throws SQLException If an SQL error occurs.
     */
    public static boolean deleteUser(UUID userId) throws SQLException {
        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();
        if (opConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = opConn.get();

        String sql = "DELETE FROM users WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, userId, java.sql.Types.OTHER);
            int rowDeleted = ps.executeUpdate();

            return rowDeleted > 0;
        } finally {
            conn.close();
        }
    }

    /**
     * Deletes a user by their email.
     *
     * @param email The email of the user to delete.
     * @return True if the user was successfully deleted, false otherwise.
     * @throws SQLException If an SQL error occurs.
     */
    public static boolean deleteUser(String email) throws SQLException {
        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();
        if (opConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = opConn.get();
        String sql = "DELETE FROM users WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            int rowDeleted = ps.executeUpdate();

            return rowDeleted > 0;
        } finally {
            conn.close();
        }
    }
}
