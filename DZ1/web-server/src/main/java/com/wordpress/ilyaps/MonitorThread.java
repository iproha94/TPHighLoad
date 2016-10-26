package com.wordpress.ilyaps;

import com.wordpress.ilyaps.graphite.SimpleGraphiteClient;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.Properties;

/**
 * Created by ilyaps on 13.04.16.
 */
public class MonitorThread implements Runnable  {
    private final static Logger LOGGER = Logger.getLogger(MonitorThread.class);

    private SimpleGraphiteClient graphiteClient;
    private OperatingSystemMXBean operatingSystemMXBean;

    private String cpuDir;
    private String nameServer;
    private String rpsDir;

    public MonitorThread(String fileName) {
        if (fileName == null) {
            throw new NullPointerException("fileName");
        }

        String graphite_host = null;
        int graphite_port = 0;

        try (final FileInputStream fis = new FileInputStream(fileName)) {
            final Properties properties = new Properties();
            properties.load(fis);

            cpuDir = properties.getProperty("cpu_dir");
            rpsDir = properties.getProperty("rps_dir");
            nameServer = properties.getProperty("name_server");

            graphite_host = properties.getProperty("graphite_host");
            graphite_port = new Integer(properties.getProperty("graphite_port"));

            LOGGER.fatal("graphite_host " + graphite_host);
            LOGGER.fatal("graphite_port " + graphite_port);
            LOGGER.info("cpuDir " + cpuDir);
            LOGGER.info("rpsDir " + rpsDir);
        } catch (IOException e) {
            LOGGER.fatal(e);
        }

        operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        graphiteClient = new SimpleGraphiteClient(graphite_host, graphite_port);
    }

    @Override
    public void run() {
        while (true) {
            int rps = HeadThread.counterRequests.getAndSet(0);
            double cpu = operatingSystemMXBean.getSystemLoadAverage() * 100 / Runtime.getRuntime().availableProcessors();

            LOGGER.info("rps " + rps + '\t' + "cpu " + cpu);

            graphiteClient.sendMetric(nameServer + cpuDir, cpu);
            graphiteClient.sendMetric(nameServer + rpsDir, rps);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
