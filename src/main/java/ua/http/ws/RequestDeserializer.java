package ua.http.ws;

import java.util.Collections;

public class RequestDeserializer {
    public RequestDeserializer() {

    }

    public Request deserializeRequest(byte[] request) {
        return new Request(Method.GET, "/", Version.HTTP_1_1, Collections.singletonMap("Host", "server.host.name"));
    }
}
