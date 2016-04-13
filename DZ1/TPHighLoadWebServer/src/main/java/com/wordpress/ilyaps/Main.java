package com.wordpress.ilyaps;

import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Main {
    private final static Logger LOGGER = Logger.getLogger(Main.class);
    public static int PORT = 8080;
    public static String DOCUMENT_ROOT = "../http-test-suite";
    public static int NUMBER_CORE = 1;
    private static final String PROPERTIES_FILE = "cfg/monitor.properties";

    public static void main(String[] args) throws ParseException {
        Option option1 = new Option("p", "port", true, "Port");
        Option option2 = new Option("c", "cpu", true, "CPU");
        Option option3 = new Option("r", "rootdir", true, "RootDir");

        Options posixOptions = new Options();
        posixOptions.addOption(option1);
        posixOptions.addOption(option2);
        posixOptions.addOption(option3);

        CommandLineParser cmdLinePosixParser = new PosixParser();
        CommandLine commandLine = cmdLinePosixParser.parse(posixOptions, args);


        if (commandLine.hasOption('r')) {
            DOCUMENT_ROOT = commandLine.getOptionValue('r');
        }
        if (commandLine.hasOption("c")) {
            NUMBER_CORE = new Integer(commandLine.getOptionValue('c'));
        }
        if (commandLine.hasOption("p")) {
            PORT = new Integer(commandLine.getOptionValue('p'));
        }

        LOGGER.fatal("PORT = " + PORT);
        LOGGER.fatal("CPU = " + NUMBER_CORE);
        LOGGER.fatal("DOCUMENT_ROOT = " + DOCUMENT_ROOT);

        Queue<SocketChannel> socketChannelQueue = new ConcurrentLinkedQueue<>();

        new Thread(new AcceptThread(PORT, socketChannelQueue)).start();
        new Thread(new MonitorThread(PROPERTIES_FILE)).start();

        for (int i = 0; i < NUMBER_CORE; ++i) {
            try {
                new Thread(new ProcessThread(socketChannelQueue)).start();
                LOGGER.info("start Process thread #" + (i+1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
