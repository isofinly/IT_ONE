package com.github.kxrxh.javalin.rest.util;

import io.prometheus.client.Collector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Collector for Jetty QueuedThreadPool metrics to be used with Prometheus.
 */
public class QueuedThreadPoolCollector extends Collector {

    private static final List<String> EMPTY_LIST = new ArrayList<>();
    private final QueuedThreadPool queuedThreadPool;

    private QueuedThreadPoolCollector(QueuedThreadPool queuedThreadPool) {
        this.queuedThreadPool = queuedThreadPool;
    }

    /**
     * Initializes the QueuedThreadPoolCollector with the provided QueuedThreadPool
     * and registers it.
     *
     * @param queuedThreadPool The QueuedThreadPool instance to collect metrics
     *                         from.
     */
    public static void initialize(QueuedThreadPool queuedThreadPool) {
        new QueuedThreadPoolCollector(queuedThreadPool).register();
    }

    private static MetricFamilySamples buildGauge(String name, String help, double value) {
        return new MetricFamilySamples(
                name,
                Type.GAUGE,
                help,
                Collections.singletonList(new MetricFamilySamples.Sample(name, EMPTY_LIST, EMPTY_LIST, value)));
    }

    /**
     * Collects metrics from the QueuedThreadPool.
     *
     * @return List of MetricFamilySamples containing metrics.
     */
    @Override
    public List<MetricFamilySamples> collect() {
        return Arrays.asList(
                buildGauge("jetty_queued_thread_pool_threads", "Number of total threads",
                        queuedThreadPool.getThreads()),
                buildGauge("jetty_queued_thread_pool_utilization", "Percentage of threads in use",
                        (double) queuedThreadPool.getThreads() / queuedThreadPool.getMaxThreads()),
                buildGauge("jetty_queued_thread_pool_threads_idle", "Number of idle threads",
                        queuedThreadPool.getIdleThreads()),
                buildGauge("jetty_queued_thread_pool_jobs", "Number of total jobs", queuedThreadPool.getQueueSize()));
    }
}
