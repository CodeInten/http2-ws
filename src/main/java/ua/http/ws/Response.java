package ua.http.ws;

import java.util.Map;

public class Response {
    final Version httpVersion;
    final int status;
    final String humanRead;
    final Map<String, String> headers;

    public Response(Version httpVersion, int status, String humanRead, Map<String, String> headers) {
        this.httpVersion = httpVersion;
        this.status = status;
        this.humanRead = humanRead;
        this.headers = headers;
    }
}
