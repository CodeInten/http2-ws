package ua.http.ws;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;

public class RequestDeserializerTest {

    private byte[] requestStringToByteArray(String stringRequest) {
        byte[] request = new byte[stringRequest.length()];
        for (int i = 0; i < stringRequest.length(); i++) {
            request[i] = (byte)stringRequest.charAt(i);
        }
        return request;
    }

    private RequestDeserializer deserializer;

    @Before
    public void setUp() throws Exception {
        deserializer = new RequestDeserializer();
    }

    @Test
    public void deserializeRequestWithHostHeader() throws Exception {
        Request httpRequest = deserializer.deserializeRequest(requestStringToByteArray(
                "GET / HTTP/1.1\r\n" +
                "Host: server.host.name\r\n" +
                "\r\n"
        ));

        assertThat(httpRequest.httpMethod, is(Method.GET));
        assertThat(httpRequest.url, is("/"));
        assertThat(httpRequest.httpVersion, is(Version.HTTP_1_1));
        assertThat(httpRequest.headers, is(Collections.singletonMap("Host", "server.host.name")));
    }

    @Test
    public void deserializeUpgradeToHttp2Request() throws Exception {
        Request httpRequest = deserializer.deserializeRequest(requestStringToByteArray(
                "GET / HTTP/1.1\r\n" +
                "Connection: Upgrade\r\n" +
                "Upgrade: h2c\r\n" +
                "\r\n"
        ));

        assertThat(httpRequest.httpMethod, is(Method.GET));
        assertThat(httpRequest.url, is("/"));
        assertThat(httpRequest.httpVersion, is(Version.HTTP_1_1));
        assertThat(httpRequest.headers, hasEntry("Connection", "Upgrade"));
        assertThat(httpRequest.headers, hasEntry("Upgrade", "h2c"));
    }
}
