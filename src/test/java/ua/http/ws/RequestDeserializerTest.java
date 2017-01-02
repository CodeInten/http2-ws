package ua.http.ws;

import com.twitter.hpack.Encoder;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.instanceOf;

public class RequestDeserializerTest {

    private byte[] requestStringToByteArray(String stringRequest) {
        byte[] request = new byte[stringRequest.length()];
        for (int i = 0; i < stringRequest.length(); i++) {
            request[i] = (byte) stringRequest.charAt(i);
        }
        return request;
    }

    @Test
    public void deserializeRequestWithHostHeader() throws Exception {
        RequestDeserializer deserializer = new RequestDeserializer(
                requestStringToByteArray(
                        "GET / HTTP/1.1\r\n" +
                                "Host: server.host.name\r\n" +
                                "\r\n"
                )
        );
        Request httpRequest = deserializer.deserializeRequest();

        assertThat(httpRequest.httpMethod, is(Method.GET));
        assertThat(httpRequest.url, is("/"));
        assertThat(httpRequest.httpVersion, is(Version.HTTP_1_1));
        assertThat(httpRequest.headers, is(Collections.singletonMap("Host", "server.host.name")));
    }

    @Test
    public void deserializeUpgradeToHttp2Request() throws Exception {
        RequestDeserializer deserializer = new RequestDeserializer(
                requestStringToByteArray(
                        "GET / HTTP/1.1\r\n" +
                                "Connection: Upgrade\r\n" +
                                "Upgrade: h2c\r\n" +
                                "\r\n"
                )
        );
        Request httpRequest = deserializer.deserializeRequest();

        assertThat(httpRequest.httpMethod, is(Method.GET));
        assertThat(httpRequest.url, is("/"));
        assertThat(httpRequest.httpVersion, is(Version.HTTP_1_1));
        assertThat(httpRequest.headers, hasEntry("Connection", "Upgrade"));
        assertThat(httpRequest.headers, hasEntry("Upgrade", "h2c"));
    }

    @Test
    public void deserializePriRequest() throws Exception {
        RequestDeserializer deserializer = new RequestDeserializer(
                requestStringToByteArray(
                        "PRI * HTTP/2.0\r\n" +
                                "\r\n" +
                                "SM\r\n" +
                                "\r\n"
                )
        );
        Request request = deserializer.deserializeRequest();

        assertThat(request.httpMethod, is(Method.PRI));
        assertThat(request.url, is("*"));
        assertThat(request.httpVersion, is(Version.HTTP_2_0));
        assertThat(request.body, is("SM"));
    }

    @Test
    public void deserializeHttp2Request_rootDirectory() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(0);out.write(0);out.write(38);
        out.write(1);out.write(5);
        out.write(0);out.write(0);out.write(0);out.write(1);

        Encoder encoder = new Encoder(4096);
        encoder.encodeHeader(out, ":scheme".getBytes(), "http".getBytes(), false);
        encoder.encodeHeader(out, ":method".getBytes(), "GET".getBytes(), false);
        encoder.encodeHeader(out, ":authority".getBytes(), "localhost:8080".getBytes(), false);
        encoder.encodeHeader(out, ":path".getBytes(), "/".getBytes(), false);
        encoder.encodeHeader(out, "accept-encoding".getBytes(), "gzip".getBytes(), false);
        encoder.encodeHeader(out, "user-agent".getBytes(), "Jetty/9.3.12.v20160915".getBytes(), false);

        RequestDeserializer deserializer = new RequestDeserializer(out.toByteArray());

        Request headers = deserializer.deserializeRequest();

        assertThat(headers, instanceOf(HeaderFrameRequest.class));
    }
}
