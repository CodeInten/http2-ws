package ua.http.ws;

import java.util.Collections;

public class RequestHandler {
    public Response handle(Request request) {
        return new Response(request.httpVersion, 200, "OK", Collections.singletonMap("Host", "localhost"));
    }
}
