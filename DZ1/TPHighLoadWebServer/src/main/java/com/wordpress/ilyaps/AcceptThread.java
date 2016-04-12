package com.wordpress.ilyaps;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Queue;

/**
 * Created by ilyaps on 12.03.16.
 */
public class AcceptThread implements Runnable{
    private final static Logger LOGGER = Logger.getLogger(AcceptThread.class);

    int port;
    Queue<SocketChannel> socketChannelQueue;

    public AcceptThread(int port, Queue socketChannelQueue) {
        this.port = port;
        this.socketChannelQueue = socketChannelQueue;
    }

    @Override
    public void run() {
        ServerSocketChannel serverSocketChannel = null;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
        } catch (IOException e) {
            LOGGER.warn(e);
        }

        while(true){
            try {
                SocketChannel socketChannel = serverSocketChannel.accept();
                socketChannelQueue.add(socketChannel);
            } catch (IOException e) {
                LOGGER.warn(e);
            }
        }
    }
}
