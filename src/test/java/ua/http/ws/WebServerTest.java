package ua.http.ws;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

public class WebServerTest {

    private void waitForInputData() throws IOException {
        while (socket.getInputStream().available() == 0) {
        }
    }

    private byte[] stringRequestToBytes(String stringRequest) {
        byte[] request = new byte[stringRequest.length()];
        for (int i = 0; i < stringRequest.length(); i++) {
            request[i] = (byte) stringRequest.charAt(i);
        }
        return request;
    }

    private void sendRequestToServer(String request) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(stringRequestToBytes(request));
        outputStream.flush();
    }

    private String readResponseFromServer() throws IOException {
        StringBuilder builder = new StringBuilder();
        InputStream inputStream = socket.getInputStream();
        byte[] response = new byte[inputStream.available()];
        inputStream.read(response);
        for (byte b : response) {
            builder.append((char) b);
        }

        return builder.toString();
    }

    private WebServer webServer;
    private Socket socket;

    @Before
    public void startServer() throws IOException, InterruptedException {
        webServer = new WebServer(new String[]{"-p", "8080"});
        webServer.start();
        socket = new Socket(InetAddress.getLocalHost(), 8080);
    }

    @After
    public void stopServer() throws Exception {
        socket.close();
        webServer.stop();
    }

    @Test
    public void clientConnectsToServer_whenServerStarted_with_8080_port() throws Exception {
        assertThat(socket.isConnected(), is(true));
    }

    @Test
    public void serverSend_200_OK_whenRootRequested() throws Exception {
        sendRequestToServer(
                "GET / HTTP/1.1\r\n" +
                        "Accept-Encoding: gzip\r\n" +
                        "User-Agent: Jetty/9.3.12.v20160915\r\n" +
                        "Host: localhost:8080\r\n" +
                        "\r\n"
        );

        waitForInputData();

        String stringResponse = readResponseFromServer();

        assertThat(stringResponse, startsWith("HTTP/1.1 200 OK\r\n"));
        assertThat(stringResponse, containsString("Host: localhost\r\n"));
        assertThat(stringResponse, containsString("Content-Length: 0\r\n"));
        assertThat(stringResponse, endsWith("\r\n"));
    }

    @Test
    public void upgradeConnectionTest() throws Exception {
        sendRequestToServer(
                "GET / HTTP/1.1\r\n" +
                        "Accept-Encoding: gzip\r\n" +
                        "User-Agent: Jetty/9.3.12.v20160915\r\n" +
                        "Connection: Upgrade\r\n" +
                        "Upgrade: h2c\r\n" +
                        "\r\n"
        );

        waitForInputData();

        String stringResponse = readResponseFromServer();

        assertThat(stringResponse, startsWith("HTTP/1.1 101 Switching Protocols\r\n"));
        assertThat(stringResponse, containsString("Content-Length: 0\r\n"));
        assertThat(stringResponse, containsString("Host: localhost\r\n"));
        assertThat(stringResponse, containsString("Connection: Upgrade\r\n"));
        assertThat(stringResponse, containsString("Upgrade: h2c\r\n"));
        assertThat(stringResponse, endsWith("\r\n"));
    }
}
