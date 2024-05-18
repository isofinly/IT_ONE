package com.github.kxrxh.javalin.rest.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import io.nats.client.Dispatcher;
import io.nats.client.Nats;

public class NATS {
    private NATS() {}
    private static io.nats.client.Connection natsConnection;

    public static void connect(String natsServerURL, String subject) throws IOException, InterruptedException {
        if (natsConnection == null) {
            natsConnection = Nats.connect(natsServerURL);
            Dispatcher dispatcher = natsConnection.createDispatcher(msg -> {
                String message = new String(msg.getData(), StandardCharsets.UTF_8);
                NATSSubscriber.executePreparedStatement(message);
            });
            dispatcher.subscribe(subject);
        }
    }

    public static void disconnect() throws InterruptedException {
        if (natsConnection != null) {
            natsConnection.close();
        }
    }
}