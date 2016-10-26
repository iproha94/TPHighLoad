package com.wordpress.ilyaps.response;

import com.wordpress.ilyaps.http.HTTPCode;

import java.util.Date;

public class HeaderResponse {
    private String contentType = "";
    private HTTPCode code = HTTPCode.UNKNOWN;
    private int contentLength = 0;
    private String date = new Date().toString();
    private String server = "Ilya Petuhov web server v 1.0";
    private String connection = "close";

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setStatus(HTTPCode code) {
        this.code = code;
    }

    public HTTPCode getCode() {
        return code;
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append("HTTP/1.1 ").append(code.getValue()).append("\r\n");
        stringBuffer.append("Server: ").append(server).append("\r\n");
        stringBuffer.append("Date: ").append(date).append("\r\n");
        stringBuffer.append("Content-Type: ").append(contentType).append("\r\n");
        stringBuffer.append("Content-Length: ").append(contentLength).append("\r\n");
        stringBuffer.append("Connection: ").append(connection).append("\r\n");
        stringBuffer.append("\r\n");

        return stringBuffer.toString();
    }
}
