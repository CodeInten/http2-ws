package ua.http.ws;

import java.util.Map;

public class ResponseSerializer {
    public byte[] serialize(Response response) {
        String stringRepresentation = getStartLine(response) +
                getHeaders(response) +
                "\r\n";
        byte[] serialized = new byte[stringRepresentation.length()];
        for (int i = 0; i < stringRepresentation.length(); i++) {
            serialized[i] = (byte)stringRepresentation.charAt(i);
        }
        return serialized;
    }

    private String getHeaders(Response response) {
        StringBuilder builder = new StringBuilder();
        for(Map.Entry<String, String> header : response.headers.entrySet()) {
            builder.append(String.format("%s: %s\r\n", header.getKey(), header.getValue()));
        }
        return builder.toString();
    }

    private String getStartLine(Response response) {
        return String.format("%s %d %s\r\n", response.httpVersion, response.status, response.humanRead);
    }
}
