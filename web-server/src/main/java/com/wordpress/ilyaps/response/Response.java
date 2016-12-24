package com.wordpress.ilyaps.response;

import com.wordpress.ilyaps.Main;
import com.wordpress.ilyaps.http.HTTPCode;
import com.wordpress.ilyaps.http.HTTPMethod;
import com.wordpress.ilyaps.request.ParseRequest;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;


/**
 * Created by ilyaps on 12.03.16.
 */
public class Response {
    private final static Logger LOGGER = Logger.getLogger(Response.class);

    static Map<String, File> cashFile = new ConcurrentHashMap<>();
    static Map<Path, byte[]> cashBytes = new ConcurrentHashMap<>();

    private static File getFile(String path) {
        File file = cashFile.get(path);
        if (file == null) {
            file = new File(path);
            cashFile.put(path, file);
        }

        return file;
    }

    private static byte[] getBytes(Path path) throws IOException {
        byte[] bytes = cashBytes.get(path);
        if (bytes == null) {
            bytes =  Files.readAllBytes(path);
            cashBytes.put(path, bytes);
        }

        return bytes;
    }


    public static ByteBuffer getResponse(String request) {
        HeaderResponse header = new HeaderResponse();

        ParseRequest parseRequest = new ParseRequest(request);
        boolean successParse = parseRequest.parse();

        if (!successParse) {
            header.setStatus(HTTPCode.BAD_REQUEST);
            return getBuffer(header, null);
        }

        String path = Main.DOCUMENT_ROOT + parseRequest.getFileName();
        File file = getFile(path);

        if (parseRequest.getMethod() == HTTPMethod.UNKNOWN) {
            header.setStatus(HTTPCode.METHOD_NOT_ALLOWED);
            return getBuffer(header, null);
        }


        if (parseRequest.isEscaping()) {
            header.setStatus(HTTPCode.FORBIDDEN);
            return getBuffer(header, null);
        }

        if (file.isDirectory()) {
            path = Main.DOCUMENT_ROOT + parseRequest.getFileName() + "index.html";
            file = getFile(path);

            if (file.exists()) {
                header.setStatus(HTTPCode.OK);
                header.setContentType(parseRequest.getContentType());
                header.setContentLength((int) file.length());

                if (parseRequest.getMethod() != HTTPMethod.HEAD) {
                    return getBuffer(header, file);
                } else {
                    return getBuffer(header, null);
                }

            } else {
                header.setStatus(HTTPCode.FORBIDDEN);
                return getBuffer(header, null);
            }
        }

        if (file.exists()) {
            header.setStatus(HTTPCode.OK);
            header.setContentType(parseRequest.getContentType());
            header.setContentLength((int) file.length());

            if (parseRequest.getMethod() != HTTPMethod.HEAD) {
                return getBuffer(header, file);
            } else {
                return getBuffer(header, null);
            }
        } else {
            header.setStatus(HTTPCode.NOT_FOUND);
            return getBuffer(header, null);
        }
    }

    private static ByteBuffer getBuffer(HeaderResponse header, File body) {
        LOGGER.info(header.toString());

        byte[] bytesHeader = header.toString().getBytes();
        byte[] bytesFile = null;

        int size = bytesHeader.length;

        if (body != null) {
            try {
                bytesFile = getBytes(body.toPath());
                size += bytesFile.length;
            } catch (IOException e) {
                LOGGER.info(e);
            }
        }

        ByteBuffer byteBuffer = ByteBuffer.allocate(size);

        byteBuffer.put(ByteBuffer.wrap(bytesHeader));

        if (bytesFile != null) {
            byteBuffer.put(ByteBuffer.wrap(bytesFile));
        }

        return byteBuffer;
    }
}
