package ua.http.ws;

import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RequestDeserializerTest {

    @Test
    public void deserializeRequestWithHostHeader() throws Exception {
        String originRequest = "GET / HTTP/1.1\r\n" +
                "Host: http.header.host\r\n" +
                "\r\n";
        byte[] request = new byte[originRequest.length()];
        for (int i = 0; i < originRequest.length(); i++) {
            request[i] = (byte)originRequest.charAt(i);
        }

        RequestDeserializer deserializer = new RequestDeserializer();

        Request httpRequest = deserializer.deserializeRequest(request);

        assertThat(httpRequest.httpMethod, is(Method.GET));
        assertThat(httpRequest.url, is("/"));
        assertThat(httpRequest.httpVersion, is(Version.HTTP_1_1));
        assertThat(httpRequest.headers, is(Collections.singletonMap("Host", "server.host.name")));
    }
}
