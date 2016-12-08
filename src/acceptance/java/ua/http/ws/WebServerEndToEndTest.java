package ua.http.ws;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.HttpVersion;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class WebServerEndToEndTest {

    private Process webServer;

    @Before
    public void startWebServer() throws IOException, InterruptedException {
        webServer = new ProcessBuilder("java", "-cp", "build/classes/main", "ua.http.ws.WebServer", "-p", "8080")
                .start();
        webServer.waitFor(1, TimeUnit.SECONDS);
    }

    @After
    public void stopWebServer() throws Exception {
        webServer.destroy();
    }

    @Test
    public void serverShouldListenToTheSpecifiedPort() throws Exception {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(InetAddress.getLocalHost(), 8080));

        assertThat(socket.isConnected(), is(true));
    }

    @Test
    public void serverShouldSendOk_whenGetRequestOnRoot() throws Exception {
        HttpClient client = new HttpClient();
        client.start();

        ContentResponse response = client.newRequest("http://localhost:8080/")
                .method(HttpMethod.GET)
                .timeout(10, TimeUnit.SECONDS)
                .send();

        assertThat(response.getStatus(), is(HttpStatus.OK_200));
        assertThat(response.getVersion(), is(HttpVersion.HTTP_1_1));
    }
}
