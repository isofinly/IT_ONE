package com.github.kxrxh.javalin.rest.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import io.nats.client.Connection;
import io.nats.client.Nats;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NATSUtil {

    private static Connection natsConnection;

    public static Connection getNatsConnection() {
        return natsConnection;
    }

    public static void setNatsConnection(Connection natsConnection) {
        NATSUtil.natsConnection = natsConnection;
    }

    private NATSUtil() {
    }

    public static void connect(String natsServerURL) throws IOException, InterruptedException {
        if (natsConnection == null) {
            natsConnection = Nats.connect(natsServerURL);
        }
    }

    public static void disconnect() throws InterruptedException {
        if (natsConnection != null) {
            natsConnection.close();
        }
    }

    public static void publish(String subject, String message) {
        if (natsConnection != null) {
            natsConnection.publish(subject, message.getBytes(StandardCharsets.UTF_8));
        } else {
            log.error("NATS connection is not established");
        }
    }
}