package com.wordpress.ilyaps;

import com.wordpress.ilyaps.response.Response;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ilyaps on 12.03.16.
 */
public class HeadThread implements Runnable {
    static int BUFFER_SIZE = 4096;

    public static AtomicInteger counterRequests = new AtomicInteger(0);

    private final static Logger LOGGER = Logger.getLogger(HeadThread.class);
    private final Selector selector;
    private int port;

    public HeadThread(int port) throws IOException {
        this.port = port;
        selector = Selector.open();
    }

    @Override
    public void run() {
        ServerSocketChannel serverSocketChannel = null;
        SelectionKey serverkey = null;

        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);
            serverkey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            LOGGER.warn(e);
        }

        while (true) {
            try {
                int ready = selector.select();

                if (ready == 0) {
                    continue;
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while(keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();

                    if (key.isWritable()) {
                        writeToSocket(key);
                    } else if (key.isReadable()) {
                        readFromSocket(key);
                    } else if (serverkey == key && key.isAcceptable()) {
                        try {
                            SocketChannel socketChannel = serverSocketChannel.accept();
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector, SelectionKey.OP_READ);
                        } catch (IOException e) {
                            LOGGER.warn(e);
                        }
                    }

                    keyIterator.remove();
                }
                selectedKeys.clear();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeToSocket(SelectionKey key) throws IOException {
        Attachment attachment = (Attachment)key.attachment();
        SocketChannel sc = (SocketChannel)key.channel();

        if (attachment == null) {
            close(key);
        } else if (attachment.getByteBuffer().hasRemaining()) {
            sc.write(attachment.getByteBuffer());
        } else {
            close(key);
        }
    }

    private void readFromSocket(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel)key.channel();
        ByteBuffer bb = ByteBuffer.allocate(BUFFER_SIZE);

        int countRead = sc.read(bb);

        if (countRead < 1) {
            close(key);
        } else {
            String str = new String(bb.array());
            ByteBuffer newbb = Response.getResponse(str);
            newbb.rewind();

            key.attach(new Attachment(newbb));
            key.interestOps(SelectionKey.OP_WRITE);
        }
    }

    public static void close(SelectionKey key) throws IOException {
        counterRequests.getAndIncrement();
        key.channel().close();
        key.cancel();
    }
}
