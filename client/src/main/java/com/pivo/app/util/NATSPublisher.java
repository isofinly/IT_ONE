package com.pivo.app.util;

import io.nats.client.Connection;
import io.nats.client.Nats;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public class NATSPublisher {
    private static Connection natsConnection = null;

    public NATSPublisher(String natsServerURL) throws IOException, InterruptedException {
        natsConnection = Nats.connect(natsServerURL);
    }

    public static void close() throws InterruptedException {
        natsConnection.close();
    }

    public void publishToNATS(String sql, List<Object> params) {
        JSONArray jsonParams = new JSONArray();
        for (Object param : params) {
            if (param instanceof LocalDateTime) {
                // Convert LocalDateTime to a standardized string format
                jsonParams.put(((LocalDateTime) param).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            } else {
                jsonParams.put(param);
            }
        }

        JSONObject message = new JSONObject()
                .put("sql", sql)
                .put("params", jsonParams);

        natsConnection.publish("finance_updates", message.toString().getBytes());
    }
}
