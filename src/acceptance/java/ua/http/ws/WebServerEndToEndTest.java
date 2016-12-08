package ua.http.ws;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
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

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class WebServerEndToEndTest {

    private Process webServer;
    private HttpClient client;

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

    @Before
    public void startWebClient() throws Exception {
        client = new HttpClient();
        client.start();
    }

    @After
    public void stopClient() throws Exception {
        client.stop();
    }

    @Test
    public void serverShouldListenToTheSpecifiedPort() throws Exception {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(InetAddress.getLocalHost(), 8080));

        assertThat(socket.isConnected(), is(true));
    }

    @Test
    public void serverShouldSendOk_whenGetRequestOnRoot() throws Exception {
        ContentResponse response = client.newRequest("http://localhost:8080/")
                .method(HttpMethod.GET)
                .timeout(10, TimeUnit.SECONDS)
                .send();

        assertThat(response.getStatus(), is(HttpStatus.Code.OK.getCode()));
        assertThat(response.getVersion(), is(HttpVersion.HTTP_1_1));
    }

    @Test
    public void serverShouldSendOk_whenClientAskForConnectionUpdateToHttp_2() throws Exception {
        ContentResponse response = client.newRequest("http://localhost:8080/")
                .method(HttpMethod.GET)
                .timeout(10, TimeUnit.SECONDS)
                .version(HttpVersion.HTTP_1_1)
                .header(HttpHeader.CONNECTION, "Upgrade")
                .header(HttpHeader.UPGRADE, "h2c")
                .send();

        assertThat(response.getStatus(), is(HttpStatus.Code.SWITCHING_PROTOCOLS.getCode()));
        assertThat(response.getVersion(), is(HttpVersion.HTTP_1_1));
        assertThat(
                response.getHeaders(),
                allOf(
                        hasItem(new HttpField(HttpHeader.CONNECTION, "Upgrade")),
                        hasItem(new HttpField(HttpHeader.UPGRADE, "h2c"))
                )
        );
    }
}
