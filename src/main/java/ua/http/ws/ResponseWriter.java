package ua.http.ws;

import java.io.IOException;
import java.io.OutputStream;

public class ResponseWriter {
    private final OutputStream out;

    public ResponseWriter(OutputStream out) {
        this.out = out;
    }

    public void writeSingleResponse(byte[] response) {
        try {
            out.write(response);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
