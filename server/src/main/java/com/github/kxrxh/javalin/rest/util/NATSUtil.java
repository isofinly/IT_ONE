package com.github.kxrxh.javalin.rest.util;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Slf4j
public class NATSUtil {

    private static Connection natsConnection;

    private NATSUtil() {
    }

    public static Connection getNatsConnection() {
        return natsConnection;
    }

    public static void setNatsConnection(Connection natsConnection) {
        NATSUtil.natsConnection = natsConnection;
    }

    public static void connect(String natsServerURL) throws IOException, InterruptedException {
        if (natsConnection == null) {
            try {
                SSLContext sslContext = SSLUtils.createSSLContext();
                Options options = new Options.Builder()
                        .server(natsServerURL)
                        .sslContext(sslContext)
//                        .connectionTimeout(Duration.ofSeconds(10))
                        .build();

                natsConnection = Nats.connect(options);
                log.info("NATS connection established");
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                throw new IOException("Failed to create SSL context", e);
            }
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
