package com.github.kxrxh.javalin.rest.util;

import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import java.io.IOException;

/**
 * Utility class for initializing and shutting down Prometheus metrics.
 */
@Slf4j
public class Prometheus {

    private static HTTPServer server;

    private Prometheus() {
    }

    /**
     * Initializes Prometheus metrics.
     *
     * @param statisticsHandler The StatisticsHandler to collect Jetty statistics.
     * @param queuedThreadPool  The QueuedThreadPool to collect Jetty thread pool
     *                          statistics.
     * @throws IOException If an I/O error occurs while initializing Prometheus.
     */
    public static void initializePrometheus(StatisticsHandler statisticsHandler, QueuedThreadPool queuedThreadPool)
            throws IOException {
        StatisticsHandlerCollector.initialize(statisticsHandler);
        DefaultExports.initialize();
        QueuedThreadPoolCollector.initialize(queuedThreadPool);

        // Start Prometheus HTTPServer
        server = new HTTPServer(7080);
        log.info("Prometheus is listening on: http://localhost:7080");
    }

    /**
     * Shuts down Prometheus metrics.
     */
    public static void shutdownPrometheus() {
        server.close();
    }
}
