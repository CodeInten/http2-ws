package ua.http.ws;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class Http2ConnectionsTest {

    private static final String HTTP_2_CONNECTION_UPGRADE_REQUEST_STRING = "GET / HTTP/1.1\n" +
            "Host: localhost\n" +
            "Connection: Upgrade, HTTP2-Settings\n" +
            "Upgrade: h2c\n" +
            "HTTP2-Settings: ";
    private RequestDeserializer deserializer;
    private RequestController controller;
    private ResponseSerializer serializer;

    @Before
    public void setUp() throws Exception {
        deserializer = new RequestDeserializer();
        controller = new RequestController();
        serializer = new ResponseSerializer();
    }

    @Test
    public void startClearTextConnection() throws Exception {
        Request request = deserializer.deserialize(HTTP_2_CONNECTION_UPGRADE_REQUEST_STRING);
        Response response = controller.handleRequest(request);

        assertThat(
                serializer.serialize(response),
                is("HTTP/1.1 Switching Protocols\n" +
                        "Connection: Upgrade\n" +
                        "Upgrade: h2c")
        );
    }

    @Test
    public void clientSendHttp2RequestAfterConnectionUpgrade() throws Exception {
        Request request = deserializer.deserialize(HTTP_2_CONNECTION_UPGRADE_REQUEST_STRING);

        Response response = controller.handleRequest(request);

        serializer.serialize(response);

        String firstHttp2RequestString = "GET / HTTP/2\n" +
                "Host: localhost\n" +
                "Url: /";

        Request localhostGetHomeUrl = deserializer.deserialize(firstHttp2RequestString);

        Response http2Response = controller.handleRequest(localhostGetHomeUrl);

        assertThat(serializer.serialize(http2Response), is("HTTP/2 GET 200 OK"));
    }
}
