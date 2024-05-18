public class AccountInvestmentsService {

    private AccountInvestmentsService() {
    }

    public static void createInvestment(UUID userId, UUID accountId, String investmentType, long marketValue, long purchasePrice, LocalDate purchaseDate, long dividends, double interestRate) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new SQLException("Could not get connection from pool");
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO account_investments (id, account_id, user_id, investment_type, market_value, purchase_price, purchase_date, dividends, interest_rate, created_at, updated_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")) {
            ps.setObject(1, UUID.randomUUID(), java.sql.Types.OTHER);
            ps.setObject(2, accountId, java.sql.Types.OTHER);
            ps.setObject(3, userId, java.sql.Types.OTHER);
            ps.setString(4, investmentType);
            ps.setLong(5, marketValue);
            ps.setLong(6, purchasePrice);
            ps.setDate(7, Date.valueOf(purchaseDate));
            ps.setLong(8, dividends);
            ps.setDouble(9, interestRate);
            ps.executeUpdate();
        }
    }

    public static AccountInvestment readInvestment(UUID userId, UUID investmentId) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new SQLException("Could not get connection from pool");
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM account_investments WHERE id = ? AND user_id = ?")) {
            ps.setObject(1, investmentId, java.sql.Types.OTHER);
            ps.setObject(2, userId, java.sql.Types.OTHER);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new AccountInvestment(
                            UUID.fromString(rs.getString("id")),
                            UUID.fromString(rs.getString("account_id")),
                            UUID.fromString(rs.getString("user_id")),
                            rs.getString("investment_type"),
                            rs.getLong("market_value"),
                            rs.getLong("purchase_price"),
                            rs.getDate("purchase_date").toLocalDate(),
                            rs.getLong("dividends"),
                            rs.getDouble("interest_rate"),
                            rs.getTimestamp("created_at").toLocalDate(),
                            rs.getTimestamp("updated_at").toLocalDate()
                    );
                } else {
                    throw new SQLException("Investment not found");
                }
            }
        }
    }

    public static void updateInvestment(UUID userId, UUID investmentId, UUID accountId, String investmentType, long marketValue, long purchasePrice, LocalDate purchaseDate, long dividends, double interestRate) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new SQLException("Could not get connection from pool");
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE account_investments SET account_id = ?, investment_type = ?, market_value = ?, purchase_price = ?, purchase_date = ?, dividends = ?, interest_rate = ?, updated_at = CURRENT_TIMESTAMP " +
                        "WHERE id = ? AND user_id = ?")) {
            ps.setObject(1, accountId, java.sql.Types.OTHER);
            ps.setString(2, investmentType);
            ps.setLong(3, marketValue);
            ps.setLong(4, purchasePrice);
            ps.setDate(5, Date.valueOf(purchaseDate));
            ps.setLong(6, dividends);
            ps.setDouble(7, interestRate);
            ps.setObject(8, investmentId, java.sql.Types.OTHER);
            ps.setObject(9, userId, java.sql.Types.OTHER);
            ps.executeUpdate();
        }
    }

    public static void deleteInvestment(UUID userId, UUID investmentId) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new SQLException("Could not get connection from pool");
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM account_investments WHERE id = ? AND user_id = ?")) {
            ps.setObject(1, investmentId, java.sql.Types.OTHER);
            ps.setObject(2, userId, java.sql.Types.OTHER);
            ps.executeUpdate();
        }
    }

    public static void calculateDividends(UUID userId, UUID investmentId) throws SQLException {
        Optional<Connection> optConn = DatabaseManager.getInstance().getConnection();
        if (optConn.isEmpty()) {
            throw new SQLException("Could not get connection from pool");
        }

        Connection conn = optConn.get();

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT dividends, interest_rate FROM account_investments WHERE id = ? AND user_id = ?")) {
            ps.setObject(1, investmentId, java.sql.Types.OTHER);
            ps.setObject(2, userId, java.sql.Types.OTHER);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long dividends = rs.getLong("dividends");
                    double interestRate = rs.getDouble("interest_rate");
                    double newDividends = dividends * (1 + (interestRate / 100));

                    // Update the dividends with the calculated interest
                    try (PreparedStatement psUpdate = conn.prepareStatement(
                            "UPDATE account_investments SET dividends = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ? AND user_id = ?")) {
                        psUpdate.setLong(1, (long) newDividends);
                        psUpdate.setObject(2, investmentId, java.sql.Types.OTHER);
                        psUpdate.setObject(3, userId, java.sql.Types.OTHER);
                        psUpdate.executeUpdate();
                    }
                } else {
                    throw new SQLException("Investment not found");
                }
            }
        }
    }
}
