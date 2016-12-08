package ua.http.ws;

import java.util.HashMap;
import java.util.Map;

public class RequestDeserializer {
    public RequestDeserializer() {

    }

    public Request deserializeRequest(byte[] request) {
        Map<String, String> headers = parseHttpHeaders(request);
        return new Request(Method.GET, "/", Version.HTTP_1_1, headers);
    }

    private Map<String, String> parseHttpHeaders(byte[] request) {
        Map<String, String> headers = new HashMap<>();
        for (int i = skipStartHttpLine(); i < getHeadersLength(request); i++) {
            StringBuilder nameBuilder = new StringBuilder();
            while (request[i] != (byte)':') {
                nameBuilder.append((char)request[i]);
                i += 1;
            }
            String headerName = nameBuilder.toString().trim();
            i += 1;
            StringBuilder valueBuilder = new StringBuilder();
            while (request[i] != (byte)'\r') {
                valueBuilder.append((char)request[i]);
                i += 1;
            }
            String headerValue = valueBuilder.toString().trim();
            headers.put(headerName, headerValue);
            i += 1;
        }
        return headers;
    }

    private int getHeadersLength(byte[] request) {
        return request.length - 2;
    }

    private int skipStartHttpLine() {
        return 16;
    }
}
