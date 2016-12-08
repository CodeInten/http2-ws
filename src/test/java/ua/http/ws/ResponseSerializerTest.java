package ua.http.ws;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

public class ResponseSerializerTest {

    private String responseBytesToString(byte[] serialized) {
        StringBuilder builder = new StringBuilder(serialized.length);
        for (byte b : serialized) {
            builder.append((char) b);
        }
        return builder.toString();
    }

    private ResponseSerializer serializer;

    @Before
    public void setUp() throws Exception {
        serializer = new ResponseSerializer();
    }

    @Test
    public void serializeOkRootResponse() throws Exception {
        Response response = new ResponseBuilder()
                .withVersion(Version.HTTP_1_1)
                .withStatusCode(200)
                .withHumanReadStatus("OK")
                .withHeader("Content-Length", "0")
                .withHeader("Host", "localhost")
                .build();

        byte[] serialized = serializer.serialize(response);

        String stringRepresentation = responseBytesToString(serialized);

        assertThat(stringRepresentation, startsWith("HTTP/1.1 200 OK\r\n"));
        assertThat(stringRepresentation, containsString("Host: localhost\r\n"));
        assertThat(stringRepresentation, containsString("Content-Length: 0\r\n"));
        assertThat(stringRepresentation, endsWith("\r\n"));
    }

    @Test
    public void serializeConnectionUpgradeResponse() throws Exception {
        Response response = new ResponseBuilder()
                .withVersion(Version.HTTP_1_1)
                .withStatusCode(101)
                .withHumanReadStatus("Switching Protocols")
                .withHeader("Connection", "Upgrade")
                .withHeader("Content-Length", "0")
                .withHeader("Upgrade", "h2c")
                .withHeader("Host", "localhost")
                .build();

        byte[] serialized = serializer.serialize(response);

        String stringRepresentation = responseBytesToString(serialized);

        assertThat(stringRepresentation, startsWith("HTTP/1.1 101 Switching Protocols\r\n"));
        assertThat(stringRepresentation, containsString("Content-Length: 0\r\n"));
        assertThat(stringRepresentation, containsString("Host: localhost\r\n"));
        assertThat(stringRepresentation, containsString("Connection: Upgrade\r\n"));
        assertThat(stringRepresentation, containsString("Upgrade: h2c\r\n"));
        assertThat(stringRepresentation, endsWith("\r\n"));
    }

    private class ResponseBuilder {
        private Map<String, String> headers = new HashMap<>();
        private Version version;
        private int statusCode;
        private String humanReadStatus;

        ResponseBuilder withVersion(Version version) {
            this.version = version;
            return this;
        }

        ResponseBuilder withStatusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        ResponseBuilder withHumanReadStatus(String humanReadStatus) {
            this.humanReadStatus = humanReadStatus;
            return this;
        }

        ResponseBuilder withHeader(String header, String value) {
            headers.put(header, value);
            return this;
        }

        Response build() {
            return new Response(version, statusCode, humanReadStatus, headers);
        }
    }
}
