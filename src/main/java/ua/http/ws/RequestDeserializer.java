package ua.http.ws;

import java.util.HashMap;
import java.util.Map;

public class RequestDeserializer {
    private final byte[] rawRequest;
    private int index;

    public RequestDeserializer(byte[] rawRequest) {
        this.rawRequest = rawRequest;
    }

    public Request deserializeRequest() {
        if (rawRequest[0] == 0) {
            return new HeaderFrameRequest();
        }
        Method method = Method.valueOf(parseMethod());
        String url = parseUrl();
        Version version = Version.valueOf(parseVersion().replace(".", "_").replace("/", "_"));
        Map<String, String> headers = parseHttpHeaders();
        return new Request(method, url, version, headers, "SM");
    }

    private String parseMethod() {
        StringBuilder builder = new StringBuilder();
        for (; rawRequest[index] != ' '; index++) {
            builder.append((char)rawRequest[index]);
        }
        index += 1;
        return builder.toString();
    }

    private String parseUrl() {
        String url = String.valueOf((char)rawRequest[index]);
        index += 2;
        return url;
    }

    private String parseVersion() {
        StringBuilder builder = new StringBuilder();
        while (notEndOfLine()) {
            builder.append((char)rawRequest[index]);
            index++;
        }
        index += 2;
        return builder.toString();
    }

    private boolean notEndOfLine() {
        return rawRequest[index] != '\r' && rawRequest[index + 1] != '\n';
    }

    private Map<String, String> parseHttpHeaders() {
        Map<String, String> headers = new HashMap<>();
        while (notEndOfLine()) {
            StringBuilder nameBuilder = new StringBuilder();
            while (rawRequest[index] != (byte)':') {
                nameBuilder.append((char)rawRequest[index]);
                index += 1;
            }
            String headerName = nameBuilder.toString().trim();
            index += 1;
            StringBuilder valueBuilder = new StringBuilder();
            while (rawRequest[index] != (byte)'\r') {
                valueBuilder.append((char)rawRequest[index]);
                index += 1;
            }
            String headerValue = valueBuilder.toString().trim();
            headers.put(headerName, headerValue);
            index += 2;
        }
        return headers;
    }
}
