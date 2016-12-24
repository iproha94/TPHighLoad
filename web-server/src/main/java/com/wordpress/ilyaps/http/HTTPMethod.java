package com.wordpress.ilyaps.http;

/**
 * Created by ilyaps on 23.02.16.
 */
public enum HTTPMethod {
    GET,
    HEAD,
    UNKNOWN;

    public static HTTPMethod getMethodByString(String method) {
        switch (method) {
            case "GET":
                return GET;
            case "HEAD":
                return HEAD;
            default:
                return UNKNOWN;
        }
    }
}
