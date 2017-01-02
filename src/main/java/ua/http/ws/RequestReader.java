package ua.http.ws;

import java.io.IOException;
import java.io.InputStream;

public class RequestReader {
    private final InputStream in;

    public RequestReader(InputStream in) {
        this.in = in;
    }

    public byte[] readSingleRequest() {
        try {
            while (in.available() == 0) {}
            byte[] requestData = new byte[in.available()];
            in.read(requestData);
            return requestData;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
