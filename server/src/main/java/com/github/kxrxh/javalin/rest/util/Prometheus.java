package com.github.kxrxh.javalin.rest.util;

import io.prometheus.client.exporter.HTTPServer;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import java.io.IOException;

@Slf4j
public class Prometheus {
    public static void initializePrometheus(StatisticsHandler statisticsHandler, QueuedThreadPool queuedThreadPool) throws IOException {
        StatisticsHandlerCollector.initialize(statisticsHandler);
        QueuedThreadPoolCollector.initialize(queuedThreadPool);
        // TODO @KXRXH
        HTTPServer prometheusServer = new HTTPServer(7080);
        log.info("Prometheus is listening on: http://localhost:7080");
    }
}
