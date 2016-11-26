package ua.http.ws;

import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ResponseWriterTest {

    @Test
    public void writeSingleResponse() throws Exception {
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream(in);

        ResponseWriter writer = new ResponseWriter(out);
        String stringRepresentation = "HTTP/1.1 200 OK\r\n";
        byte[] serialized = new byte[stringRepresentation.length()];
        for (int i = 0; i < stringRepresentation.length(); i++) {
            serialized[i] = (byte)stringRepresentation.charAt(i);
        }

        writer.writeSingleResponse(serialized);

        byte[] read = new byte[serialized.length];
        int size = in.read(read);

        assertThat(size, is(serialized.length));
        assertThat(read, is(serialized));
    }
}
