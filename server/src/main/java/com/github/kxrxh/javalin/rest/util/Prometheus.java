package com.github.kxrxh.javalin.rest.util;

import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import java.io.IOException;

@Slf4j
public class Prometheus {

    private static HTTPServer server;

    private Prometheus() {
    }

    public static void initializePrometheus(StatisticsHandler statisticsHandler, QueuedThreadPool queuedThreadPool)
            throws IOException {
        StatisticsHandlerCollector.initialize(statisticsHandler);
        DefaultExports.initialize();
        QueuedThreadPoolCollector.initialize(queuedThreadPool);
        // TODO: Move to a separate route so it can be accessed from the Web DNS
        server = new HTTPServer(7080);
        log.info("Prometheus is listening on: http://localhost:7080");
    }

    public static void shutdownPrometheus() {
        server.close();
    }
}
