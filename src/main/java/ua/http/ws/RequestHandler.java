package ua.http.ws;

import java.util.HashMap;
import java.util.Map;

public class RequestHandler {
    public Response handle(Request request) {
        Map<String, String> headers = new HashMap<>();
        int status;
        String humanRead;
        if (request.headers.containsKey("Connection")) {
            headers.putAll(request.headers);
            status = 101;
            humanRead = "Switching Protocols";
        } else {
            status = 200;
            humanRead = "OK";
        }
        headers.put("Host", "localhost");
        headers.put("Content-Length", "0");
        return new Response(request.httpVersion, status, humanRead, headers);
    }
}
