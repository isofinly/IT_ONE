package com.github.kxrxh.javalin.rest.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.SSLContext;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for managing NATS connections and publishing messages.
 */
@Slf4j
public class NATSUtil {

    /**
     * -- GETTER --
     *  Gets the NATS connection.
     *
     */
    @Getter
    private static Connection natsConnection;

    private NATSUtil() {
    }

    /**
     * Sets the NATS connection.
     *
     * @param natsConnection The NATS connection to set.
     */
    public static void setNatsConnection(Connection natsConnection) {
        NATSUtil.natsConnection = natsConnection;
    }

    /**
     * Connects to the NATS server.
     *
     * @param natsServerURL The URL of the NATS server.
     * @throws IOException          If an I/O error occurs.
     */
    public static void connect(String natsServerURL) throws IOException {
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

    /**
     * Disconnects from the NATS server.
     *
     * @throws InterruptedException If the disconnection process is interrupted.
     */
    public static void disconnect() throws InterruptedException {
        if (natsConnection != null) {
            natsConnection.close();
        }
    }

    /**
     * Publishes a message to the NATS server.
     *
     * @param subject The subject of the message.
     * @param message The message to publish.
     */
    public static void publish(String subject, String message) {
        if (natsConnection != null) {
            natsConnection.publish(subject, message.getBytes(StandardCharsets.UTF_8));
        } else {
            log.error("NATS connection is not established");
        }
    }
}
