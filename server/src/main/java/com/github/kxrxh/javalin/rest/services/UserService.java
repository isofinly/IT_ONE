package com.github.kxrxh.javalin.rest.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import com.github.kxrxh.javalin.rest.database.DatabaseManager;
import com.github.kxrxh.javalin.rest.database.GettingConnectionException;
import com.github.kxrxh.javalin.rest.database.models.User;

public class UserService {
    private static final String USER_ID = "user_id";
    private static final String EMAIL = "email";
    private static final String PASSWORD_DIGEST = "password_digest";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String FAMILY_ID = "family_id";

    private UserService() {
    }

    public static Optional<User> createUser(UUID userId, String email, String password) throws SQLException {
        String sql;
        if (userId == null) {
            sql = "INSERT INTO users (email, password_digest) VALUES (?,?)";
        } else {
            sql = "INSERT INTO users (user_id, email, password_digest) VALUES (?,?,?)";
        }

        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();
        if (opConn.isEmpty()) {
            throw new GettingConnectionException();
        }

        Connection conn = opConn.get();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int count = 1;
            if (userId != null) {
                ps.setObject(count++, userId, java.sql.Types.OTHER);
            }
            ps.setString(count++, email);
            ps.setString(count, password); // Assuming password is already hashed

            int rowInserted = ps.executeUpdate();

            if (rowInserted != 0) {
                String selectQuery = "SELECT user_id, email, password_digest FROM users WHERE email = ? AND password_digest =?";
                try (PreparedStatement ps2 = conn.prepareStatement(selectQuery)) {
                    ps2.setString(1, email);
                    ps2.setString(2, password);
                    ResultSet rs = ps2.executeQuery();
                    if (rs.next()) {
                        User user = User.builder()
                                .userId(UUID.fromString(rs.getString(USER_ID)))
                                .email(rs.getString(EMAIL))
                                .passwordDigest(rs.getString(PASSWORD_DIGEST))
                                .build();
                        rs.close();
                        return Optional.of(user);
                    }
                    rs.close();
                } catch (SQLException e) {
                    throw new SQLException("Could not insert new user", e);
                }
            }
        }
        return Optional.empty();
    }

    public static Optional<User> createUser(String email, String password) throws SQLException {
        return createUser(null, email, password);
    }

    public static Optional<User> getUser(UUID userId) throws SQLException {

        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();
        if (opConn.isEmpty()) {
            throw new GettingConnectionException();
        }

        Connection conn = opConn.get();

        String sql = "SELECT * password_digest FROM users WHERE user_id =?";
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
                rs.close();
                return Optional.of(user);
            }
            rs.close();
        }
        return Optional.empty();
    }

    public static Optional<User> getUser(String email) throws SQLException {
        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();
        if (opConn.isEmpty()) {
            throw new GettingConnectionException();
        }

        Connection conn = opConn.get();

        String sql = "SELECT * FROM users WHERE email =?";
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
            rs.close();
        }

        return Optional.empty();
    }

    public static Optional<User> getUser(String email, String password) throws SQLException {
        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();
        if (opConn.isEmpty()) {
            throw new GettingConnectionException();
        }

        Connection conn = opConn.get();

        String sql = "SELECT * FROM users WHERE email =? AND password_digest =?";
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
        }
        return Optional.empty();
    }

    public static boolean deleteUser(UUID userId) throws SQLException {
        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();
        if (opConn.isEmpty()) {
            throw new GettingConnectionException();
        }

        Connection conn = opConn.get();

        String sql = "DELETE FROM users WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, userId, java.sql.Types.OTHER);
            int rowDeleted = ps.executeUpdate();
            return rowDeleted > 0;
        }
    }

    public static boolean deleteUser(String email) throws SQLException {
        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();
        if (opConn.isEmpty()) {
            throw new GettingConnectionException();
        }

        Connection conn = opConn.get();
        String sql = "DELETE FROM users WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            int rowDeleted = ps.executeUpdate();
            return rowDeleted > 0;
        }
    }
}
