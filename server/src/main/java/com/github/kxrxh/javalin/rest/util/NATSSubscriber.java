package com.github.kxrxh.javalin.rest.util;

import com.github.kxrxh.javalin.rest.database.ConnectionRetrievingException;
import com.github.kxrxh.javalin.rest.database.DatabaseManager;
import io.nats.client.Dispatcher;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

@Slf4j
public class NATSSubscriber {

    private NATSSubscriber() {
    }

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
            } else if (param instanceof Integer) {
                pstmt.setInt(i + 1, (Integer) param);
            } else if (param instanceof Double) {
                pstmt.setDouble(i + 1, (Double) param);
            } else {
                pstmt.setObject(i + 1, param);
            }
        }
    }

    public static void subscribe(String subject) throws IOException, InterruptedException {
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