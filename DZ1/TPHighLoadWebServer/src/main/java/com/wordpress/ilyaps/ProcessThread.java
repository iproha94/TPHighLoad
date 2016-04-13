package com.wordpress.ilyaps;

import com.wordpress.ilyaps.response.Response;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ilyaps on 12.03.16.
 */
public class ProcessThread implements Runnable {
    private final static Logger LOGGER = Logger.getLogger(ProcessThread.class);
    static int BUFFER_SIZE = 4096;

    public static AtomicInteger counterRequests = new AtomicInteger(0);

    private final Queue<SocketChannel> socketChannelQueue;
    private final Selector selector;


    public ProcessThread(Queue<SocketChannel> socketChannelQueue) throws IOException {
        this.socketChannelQueue = socketChannelQueue;
        selector = Selector.open();
    }

    @Override
    public void run() {
        while (true) {
            try {
                takeNewSockets();
                int ready = selector.select(200);

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
                    }

                    keyIterator.remove();
                }
                selectedKeys.clear();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void takeNewSockets() throws IOException {

        SocketChannel newSocketChannel = socketChannelQueue.poll();
        while(newSocketChannel != null) {
            LOGGER.info("Thread name = " + Thread.currentThread().getName()
                    + "| socketQueue.length = " + socketChannelQueue.size());
            newSocketChannel.configureBlocking(false);
            newSocketChannel.register(selector, SelectionKey.OP_READ);
            newSocketChannel = socketChannelQueue.poll();
        }
    }

    private void writeToSocket(SelectionKey key) throws IOException {
        Attachment attachment = (Attachment)key.attachment();
        SocketChannel sc = (SocketChannel)key.channel();

        if (attachment == null) {
            close(key);
            return;
        }

        while (attachment.getByteBuffer().hasRemaining()) {
            sc.write(attachment.getByteBuffer());
        }
        close(key);
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
