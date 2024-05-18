package com.github.kxrxh.javalin.rest.services;


import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import com.github.kxrxh.javalin.rest.database.ConnectionRetrievingException;
import com.github.kxrxh.javalin.rest.database.DatabaseManager;
import com.github.kxrxh.javalin.rest.database.models.AccountOtherAsset;

public class AccountOtherAssetsService extends AbstractService {

    public static void createAsset(UUID userId, UUID accountId, String assetType, long purchasePrice, long currentValue,
            LocalDate purchaseDate, double depreciationRate) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO account_other_assets (id, account_id, user_id, asset_type, purchase_price, current_value, purchase_date, depreciation_rate, created_at, updated_at) "
                        +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")) {
            ps.setObject(1, UUID.randomUUID(), java.sql.Types.OTHER);
            ps.setObject(2, accountId, java.sql.Types.OTHER);
            ps.setObject(3, userId, java.sql.Types.OTHER);
            ps.setString(4, assetType);
            ps.setLong(5, purchasePrice);
            ps.setLong(6, currentValue);
            ps.setDate(7, Date.valueOf(purchaseDate));
            ps.setDouble(8, depreciationRate);
            ps.executeUpdate();
        }
    }

    public static AccountOtherAsset readAsset(UUID userId, UUID assetId) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM account_other_assets WHERE id = ? AND user_id = ?")) {
            ps.setObject(1, assetId, java.sql.Types.OTHER);
            ps.setObject(2, userId, java.sql.Types.OTHER);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return AccountOtherAsset.builder()
                            .id(UUID.fromString(rs.getString("id")))
                            .accountId(UUID.fromString(rs.getString("account_id")))
                            .userId(UUID.fromString(rs.getString("user_id")))
                            .assetType(rs.getString("asset_type"))
                            .purchasePrice(rs.getLong("purchase_price"))
                            .currentValue(rs.getLong("current_value"))
                            .purchaseDate(rs.getDate("purchase_date").toLocalDate())
                            .depreciationRate(rs.getDouble("depreciation_rate"))
                            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                            .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                            .build();
                } else {
                    throw new SQLException("Asset not found");
                }
            }
        }
    }

    public static void updateAsset(UUID userId, UUID assetId, UUID accountId, String assetType, long purchasePrice,
            long currentValue, LocalDate purchaseDate, double depreciationRate) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE account_other_assets SET account_id = ?, asset_type = ?, purchase_price = ?, current_value = ?, purchase_date = ?, depreciation_rate = ?, updated_at = CURRENT_TIMESTAMP "
                        +
                        "WHERE id = ? AND user_id = ?")) {
            ps.setObject(1, accountId, java.sql.Types.OTHER);
            ps.setString(2, assetType);
            ps.setLong(3, purchasePrice);
            ps.setLong(4, currentValue);
            ps.setDate(5, Date.valueOf(purchaseDate));
            ps.setDouble(6, depreciationRate);
            ps.setObject(7, assetId, java.sql.Types.OTHER);
            ps.setObject(8, userId, java.sql.Types.OTHER);
            ps.executeUpdate();
        }
    }

    public static void deleteAsset(UUID userId, UUID assetId) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM account_other_assets WHERE id = ? AND user_id = ?")) {
            ps.setObject(1, assetId, java.sql.Types.OTHER);
            ps.setObject(2, userId, java.sql.Types.OTHER);
            ps.executeUpdate();
        }
    }

    public static void applyDepreciation(UUID userId, UUID assetId, int years) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new ConnectionRetrievingException();
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM account_other_assets WHERE id = ? AND user_id = ?")) {
            ps.setObject(1, assetId, java.sql.Types.OTHER);
            ps.setObject(2, userId, java.sql.Types.OTHER);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long currentValue = rs.getLong("current_value");
                    double depreciationRate = rs.getDouble("depreciation_rate");
                    long newCurrentValue = (long) (currentValue * Math.pow(1 - depreciationRate / 100, years));

                    try (PreparedStatement psUpdate = conn.prepareStatement(
                            "UPDATE account_other_assets SET current_value = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ? AND user_id = ?")) {
                        psUpdate.setLong(1, newCurrentValue);
                        psUpdate.setObject(2, assetId, java.sql.Types.OTHER);
                        psUpdate.setObject(3, userId, java.sql.Types.OTHER);
                        psUpdate.executeUpdate();
                    }
                } else {
                    throw new SQLException("Asset not found");
                }
            }
        }
    }
}
