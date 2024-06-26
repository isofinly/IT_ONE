package com.github.kxrxh.javalin.rest.util;

import com.github.kxrxh.javalin.rest.database.ConnectionRetrievingException;
import com.github.kxrxh.javalin.rest.database.DatabaseManager;
import io.nats.client.Dispatcher;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

/**
 * Utility class for subscribing to NATS messages and executing SQL prepared
 * statements.
 */
@Slf4j
public class NATSSubscriber {

    private NATSSubscriber() {
    }

    /**
     * Executes a prepared statement based on the provided message.
     *
     * @param message The message containing SQL and parameters.
     */
    public static void executePreparedStatement(String message) {
        JSONObject json = new JSONObject(message);
        String sql = json.getString("sql");
        JSONArray paramsJson = json.getJSONArray("params");

        Optional<Connection> opConn = DatabaseManager.getInstance().getConnection();

        if (opConn.isEmpty()) {
            log.error("Could not get connection from database manager", new ConnectionRetrievingException());
            return;
        }

        Connection conn = opConn.get();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setPreparedStatementParams(pstmt, paramsJson);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error executing prepared statement: {}", e.getMessage());
        }
    }

    private static void setPreparedStatementParams(PreparedStatement pstmt, JSONArray paramsJson) throws SQLException {
        for (int i = 0; i < paramsJson.length(); i++) {
            Object param = paramsJson.get(i);
            if (param instanceof String str) {
                try {
                    Timestamp ts = Timestamp.valueOf(str);
                    pstmt.setTimestamp(i + 1, ts);
                } catch (IllegalArgumentException e) {
                    pstmt.setString(i + 1, str);
                }
            } else if (param instanceof Integer integer) {
                pstmt.setInt(i + 1, integer);
            } else if (param instanceof Double dbl) {
                pstmt.setDouble(i + 1, dbl);
            } else {
                pstmt.setObject(i + 1, param);
            }
        }
    }

    /**
     * Subscribes to NATS messages on the specified subject and executes prepared
     * statements.
     *
     * @param subject The subject to subscribe to.
     */
    public static void subscribe(String subject) {
        if (NATSUtil.getNatsConnection() != null) {
            Dispatcher dispatcher = NATSUtil.getNatsConnection().createDispatcher(msg -> {
                String message = new String(msg.getData(), StandardCharsets.UTF_8);
                executePreparedStatement(message);
            });
            dispatcher.subscribe(subject);
        } else {
            log.error("NATS connection is not established");
        }
    }
}
