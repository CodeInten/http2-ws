package ua.http.ws;

import java.util.Map;

public class Request {

    final Method httpMethod;
    final String url;
    final Version httpVersion;
    final Map<String, String> headers;

    public Request(Method httpMethod, String url, Version httpVersion, Map<String, String> headers) {
        this.httpMethod = httpMethod;
        this.url = url;
        this.httpVersion = httpVersion;
        this.headers = headers;
    }
}
