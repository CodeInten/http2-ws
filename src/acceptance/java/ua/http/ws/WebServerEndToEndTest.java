package ua.http.ws;

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
        webServer = new ProcessBuilder("java", "-cp", "build/classes/main", "ua.http.ws.WebServer", "-p", "8080").start();
        webServer.waitFor(5, TimeUnit.SECONDS);
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

}
