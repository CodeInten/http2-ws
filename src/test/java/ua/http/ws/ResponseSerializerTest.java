package ua.http.ws;

import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ResponseSerializerTest {

    @Test
    public void serializeOkRootResponse() throws Exception {
        Response response = new Response(Version.HTTP_1_1, 200, "OK", Collections.singletonMap("Host", "localhost"));

        ResponseSerializer responseSerializer = new ResponseSerializer();

        String stringRepresentation = "HTTP/1.1 200 OK\r\n" +
//                "Host: localhost\r\n" +
//                "Connection: Closed\r\n" +
                "Content-Length: 0\r\n" +
                "\r\n";
        byte[] serialized = new byte[stringRepresentation.length()];
        for (int i = 0; i < stringRepresentation.length(); i++) {
            serialized[i] = (byte)stringRepresentation.charAt(i);
        }

        assertThat(responseSerializer.serialize(response), is(serialized));
    }
}
