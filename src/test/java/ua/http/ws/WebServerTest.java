package ua.http.ws;

import org.junit.After;
import org.junit.Test;

import java.net.InetAddress;
import java.net.Socket;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class WebServerTest {

    private WebServer webServer;

    @Test
    public void clientConnectsToServer_whenServerStarted_with_8080_port() throws Exception {
        webServer = new WebServer(new String[] {"-p", "8080"});
        webServer.start();

        Socket socket = new Socket(InetAddress.getLocalHost(), 8080);

        assertThat(socket.isConnected(), is(true));
    }

    @After
    public void tearDown() throws Exception {
        webServer.stop();
    }
}
