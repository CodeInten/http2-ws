package ua.http.ws;

import org.junit.Test;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RequestReaderTest {

    @Test
    public void readRootDirRequest() throws Exception {
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream(in);

        RequestReader reader = new RequestReader(in);

        String originRequest = "GET / HTTP/1.1\r\n" +
                "Host: http.header.host\r\n" +
                "\r\n";
        byte[] requestData = new byte[originRequest.length()];
        for (int i = 0; i < originRequest.length(); i++) {
            requestData[i] = (byte)originRequest.charAt(i);
        }
        out.write(requestData);
        out.flush();

        assertThat(reader.readSingleRequest(), is(requestData));
        assertThat(in.available(), is(0));
    }
}
