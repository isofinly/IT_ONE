package com.github.kxrxh.javalin.rest.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import com.github.kxrxh.javalin.rest.database.models.Users;

public class DatabaseUserManager {
    private DatabaseUserManager() {
    }

    public static Optional<Users> createUser(Long userId, String username, String password) throws SQLException {
        String sql;
        if (userId == null) {
            sql = "INSERT INTO users (username, password) VALUES (?,?)";
        } else {
            sql = "INSERT INTO users (user_id, username, password) VALUES (?,?,?)";
        }
        try (PreparedStatement ps = DatabaseManager.getInstance().getConnection().prepareStatement(sql)) {
            int count = 1;
            if (userId != null) {
                ps.setLong(count++, userId);
            }
            ps.setString(count++, username);
            ps.setString(count, password);

            int rowInserted = ps.executeUpdate();

            if (rowInserted != 0) {
                String selectQuery = "SELECT user_id, username, password FROM users WHERE username = ? AND password =?";
                try (PreparedStatement ps2 = DatabaseManager.getInstance().getConnection()
                        .prepareStatement(selectQuery)) {
                    ps2.setString(1, username);
                    ps2.setString(2, password);
                    ResultSet rs = ps2.executeQuery();
                    if (rs.next()) {
                        Users user = new Users((long) rs.getDouble("user_id"), rs.getString("username"), rs.getString("password"));
                        rs.close();
                        return Optional.of(user);
                    }
                    rs.close();
                } catch (SQLException e) {
                    // TODO: handle more gracefully
                    System.out.println("SOSI ZHOPU SQLException: " + e.getMessage());
                }
            }
        }
        return Optional.empty();

    }

    public static Optional<Users> getUser(Long userId) throws SQLException {
        String sql = "SELECT user_id, username, password FROM users WHERE user_id =?";

        try (PreparedStatement ps = DatabaseManager.getInstance().getConnection().prepareStatement(sql)) {
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Users user = new Users(rs.getLong("user_id"), rs.getString("username"), rs.getString("password"));
                rs.close();
                return Optional.of(user);
            }
            rs.close();
        }
        return Optional.empty();
    }

    public static Optional<Users> getUser(String username) throws SQLException {
        String sql = "SELECT user_id, username, password FROM users WHERE username =?";
        try (PreparedStatement ps = DatabaseManager.getInstance().getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Users user = new Users(rs.getLong("user_id"), rs.getString("username"), rs.getString("password"));
                rs.close();
                return Optional.of(user);
            }
            rs.close();
        }

        return Optional.empty();
    }

    public static Optional<Users> getUser(String username, String password) throws SQLException {
        String sql = "SELECT user_id, username, password FROM users WHERE username =? AND password =?";
        try (PreparedStatement ps = DatabaseManager.getInstance().getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Users user = new Users(rs.getLong("user_id"), rs.getString("username"), rs.getString("password"));
                rs.close();
                return Optional.of(user);
            }
            rs.close();
        }
        return Optional.empty();
    }

    public static boolean deleteUser(Long userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (PreparedStatement ps = DatabaseManager.getInstance().getConnection().prepareStatement(sql)) {
            ps.setLong(1, userId);
            int rowDeleted = ps.executeUpdate();
            return rowDeleted > 0;
        }
    }

    public static boolean deleteUser(String username) throws SQLException {
        String sql = "DELETE FROM users WHERE username = ?";
        try (PreparedStatement ps = DatabaseManager.getInstance().getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            int rowDeleted = ps.executeUpdate();
            return rowDeleted > 0;
        }
    }

    public static Optional<Users> createUser(String username, String password) throws SQLException {
        return createUser(null, username, password);
    }
}
