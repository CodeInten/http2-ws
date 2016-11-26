package ua.http.ws;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class WebServerTest {

    private WebServer webServer;

    @Test
    public void clientConnectsToServer_whenServerStarted_with_8080_port() throws Exception {
        Socket socket = new Socket(InetAddress.getLocalHost(), 8080);

        assertThat(socket.isConnected(), is(true));
    }

    @Test
    public void serverSend_200_OK_whenRootRequested() throws Exception {
        Socket socket = new Socket(InetAddress.getLocalHost(), 8080);

        String originRequest = "GET / HTTP/1.1\r\n" +
                "Accept-Encoding: gzip\r\n" +
                "User-Agent: Jetty/9.3.12.v20160915\r\n" +
                "Host: localhost:8080\r\n" +
                "\r\n";
        byte[] request = new byte[originRequest.length()];
        for (int i = 0; i < originRequest.length(); i++) {
            request[i] = (byte)originRequest.charAt(i);
        }
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(request);
        outputStream.flush();

        InputStream inputStream = socket.getInputStream();
        StringBuilder builder = new StringBuilder();

        while (inputStream.available() == 0) {}

        byte[] response = new byte[inputStream.available()];
        inputStream.read(response);
        for (byte b: response){
            builder.append((char)b);
        }

        assertThat(builder.toString(), is(
                "HTTP/1.1 200 OK\r\n" +
                "Content-Length: 0\r\n" +
                        "\r\n"
        ));
    }

    @Before
    public void startServer() throws IOException, InterruptedException {
        webServer = new WebServer(new String[] {"-p", "8080"});
        webServer.start();
    }

    @After
    public void stopServer() throws Exception {
        webServer.stop();
    }
}
