package ua.http.ws;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpMethod;
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
                .onRequestBegin((r) -> System.out.println("Request has begun"))
                .onRequestCommit((r) -> System.out.println("Request has been committed"))
                .onRequestSuccess((r) -> System.out.println("Request has succeed"))
                .onResponseBegin((r) -> {
                    System.out.println(r);
                    System.out.println(r.getHeaders());
                    System.out.println("Response has began");
                })
                .send();

        assertThat(response.getStatus(), is(200));
    }

}
