package ua.http.ws;

import com.twitter.hpack.Encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public class ResponseSerializer {
    public byte[] serialize(Response response) {
        if (response instanceof HeaderFrameResponse) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                out.write(new byte[]{0, 0, 1, 1, 5, 0, 0, 0, 1});
                Encoder encoder = new Encoder(4096);
                encoder.encodeHeader(out, ":status".getBytes(), "200".getBytes(), false);
            } catch (IOException e) {}
            return out.toByteArray();
        }
        if (response instanceof SettingFrame) {
            return new byte[] {0, 0, 0, 4, 1, 0, 0, 0, 0};
        }
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
