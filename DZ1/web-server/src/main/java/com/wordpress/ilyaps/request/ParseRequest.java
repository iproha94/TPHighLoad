package com.wordpress.ilyaps.request;

import com.wordpress.ilyaps.http.HTTPMethod;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static java.net.URLDecoder.decode;

public class ParseRequest {
    private final static Logger LOGGER = Logger.getLogger(ParseRequest.class);

    private String fileName;
    private HTTPMethod method;
    private String contentType;
    private boolean isEscaping;

    private String request;

    private static final Map<String, String> typeToContentType = new HashMap<>();
    static {
        typeToContentType.put(".html", "text/html");
        typeToContentType.put(".css", "text/css");
        typeToContentType.put(".js", "text/javascript");
        typeToContentType.put(".jpg", "image/jpeg");
        typeToContentType.put(".jpeg", "image/jpeg");
        typeToContentType.put(".png", "image/png");
        typeToContentType.put(".gif", "image/gif");
        typeToContentType.put(".swf", "application/x-shockwave-flash");
        typeToContentType.put(".ico", "image/x-icon");
    }

    public ParseRequest(String strRequest) {
        this.request = strRequest;
    }

    public boolean parse() {
        LOGGER.info(request);

        String tempRequest = request;

        int numSpace = tempRequest.indexOf(' ');

        if (numSpace < 2) {
           return false;
        }

        this.method = HTTPMethod.getMethodByString(tempRequest.substring(0, numSpace));
        tempRequest = tempRequest.substring(numSpace + 1);

        numSpace = tempRequest.indexOf(' ');
        int numWhat = tempRequest.indexOf('?');

        try {
            int numFinish = numWhat > 0 ? numWhat : numSpace;

            if (numFinish < 1) {
                return false;
            }

            fileName = decode(tempRequest.substring(0, numFinish),  "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.info(e);
        }

        isEscaping = fileNameIsEscaping(fileName);

        int numPoint = fileName.lastIndexOf('.');
        if (numPoint < 1) {
            contentType = null;
        } else {
            String rash = fileName.substring(numPoint, fileName.length());
            contentType = typeToContentType.get(rash);

        }

        if (contentType == null) {
            contentType = "UNKNOWN_TYPE";
        }

        return true;
    }

    private boolean fileNameIsEscaping(String fileName) {
        if (!fileName.contains("../")) {
            return false;
        }

        String strs[] = fileName.split("/");
        int numSlash = 0;
        int numPoints = 0;
        boolean flag = false;

        for (String str : strs) {
            if (str.equals("..")) {
                numPoints++;
            } else {
                numSlash++;
            }

            if (numPoints > numSlash) {
                flag = true;
                break;
            }
        }

        return flag;
    }

    public String getFileName() {
        return fileName;
    }

    public HTTPMethod getMethod() {
        return method;
    }

    public String getContentType() {
        return contentType;
    }

    public boolean isEscaping() {
        return isEscaping;
    }

}
