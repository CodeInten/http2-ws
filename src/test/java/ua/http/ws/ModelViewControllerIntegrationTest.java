package ua.http.ws;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http2.client.HTTP2Client;
import org.eclipse.jetty.http2.client.http.HttpClientTransportOverHTTP2;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ModelViewControllerIntegrationTest {

    private HttpServer server;
    private HttpClient client;

    @Before
    public void setUp() throws Exception {
        server = new HttpServer();
        server.start();

        HTTP2Client lowLevelClient = new HTTP2Client();
        lowLevelClient.start();

        client = new HttpClient(new HttpClientTransportOverHTTP2(lowLevelClient), null);
        client.start();
    }

    @After
    public void tearDown() throws Exception {
        server.stop();

        client.stop();
    }

    @Test
    public void handleRequest() throws Exception {
        assertThat(200, is(client.GET("http://localhost:8080").getStatus()));
    }
}
