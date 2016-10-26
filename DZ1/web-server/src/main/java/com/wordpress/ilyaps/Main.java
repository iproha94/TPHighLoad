package com.wordpress.ilyaps;

import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Main {
    public static int PORT = 8080;
    public static String DOCUMENT_ROOT = "../http-test-suite";

    private final static Logger LOGGER = Logger.getLogger(Main.class);
    private static final String PROPERTIES_FILE = "cfg/monitor.properties";

    public static void main(String[] args) throws ParseException {
        Option option1 = new Option("p", "port", true, "Port");
        Option option3 = new Option("r", "rootdir", true, "RootDir");

        Options posixOptions = new Options();
        posixOptions.addOption(option1);
        posixOptions.addOption(option3);

        CommandLineParser cmdLinePosixParser = new PosixParser();
        CommandLine commandLine = cmdLinePosixParser.parse(posixOptions, args);


        if (commandLine.hasOption('r')) {
            DOCUMENT_ROOT = commandLine.getOptionValue('r');
        }

        if (commandLine.hasOption("p")) {
            PORT = new Integer(commandLine.getOptionValue('p'));
        }

        LOGGER.fatal("PORT = " + PORT);
        LOGGER.fatal("DOCUMENT_ROOT = " + DOCUMENT_ROOT);

        new Thread(new MonitorThread(PROPERTIES_FILE)).start();

        try {
            new Thread(new HeadThread(PORT)).start();
        } catch (IOException e) {
            LOGGER.fatal(e);
        }

    }
}
