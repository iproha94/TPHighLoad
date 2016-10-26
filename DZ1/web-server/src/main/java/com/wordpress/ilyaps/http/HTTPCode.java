package com.wordpress.ilyaps.http;

/**
 * Created by ilyaps on 23.02.16.
 */
public enum HTTPCode {
    OK ("200 OK"),
    BAD_REQUEST ("400 Bad Request"),
    FORBIDDEN ("403 Forbidden"),
    NOT_FOUND ("404 Not Found"),
    METHOD_NOT_ALLOWED ("405 Method Not Allowed"),
    UNKNOWN ("0 UNKNOWN");

    private final String text;

    HTTPCode(String code) {
        this.text = code;
    }

    public String getValue() {
        return text;
    }

}
