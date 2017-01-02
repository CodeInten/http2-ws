package ua.http.ws;

import com.twitter.hpack.Decoder;
import com.twitter.hpack.Encoder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assume.assumeThat;

public class WebServerTest {

    private void waitForResponse() throws IOException {
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
        byte[] bytes = getBytes(request);
        sendRequestToServer(bytes);
    }

    private void sendRequestToServer(byte[] bytes) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(bytes);
        outputStream.flush();
    }

    private byte[] getBytes(String request) {
        return stringRequestToBytes(request);
    }

    private String readResponseFromServer() throws IOException {
        StringBuilder builder = new StringBuilder();
        byte[] response = readResponseAsByteArray();
        for (byte b : response) {
            builder.append((char) b);
        }

        return builder.toString();
    }

    private byte[] readResponseAsByteArray() throws IOException {
        InputStream inputStream = socket.getInputStream();
        byte[] response = new byte[inputStream.available()];
        inputStream.read(response);
        return response;
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

        waitForResponse();

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

        waitForResponse();

        String stringResponse = readResponseFromServer();

        assertThat(stringResponse, startsWith("HTTP/1.1 101 Switching Protocols\r\n"));
        assertThat(stringResponse, containsString("Content-Length: 0\r\n"));
        assertThat(stringResponse, containsString("Host: localhost\r\n"));
        assertThat(stringResponse, containsString("Connection: Upgrade\r\n"));
        assertThat(stringResponse, containsString("Upgrade: h2c\r\n"));
        assertThat(stringResponse, endsWith("\r\n"));
    }

    @Test
    public void twoRequestOnSameUrl() throws Exception {
        sendRequestToServer(
                "GET / HTTP/1.1\r\n" +
                        "Accept-Encoding: gzip\r\n" +
                        "User-Agent: Jetty/9.3.12.v20160915\r\n" +
                        "Host: localhost:8080\r\n" +
                        "\r\n"
        );

        waitForResponse();

        String firstResponse = readResponseFromServer();

        assertThat(firstResponse, startsWith("HTTP/1.1 200 OK\r\n"));
        assertThat(firstResponse, containsString("Host: localhost\r\n"));
        assertThat(firstResponse, containsString("Content-Length: 0\r\n"));
        assertThat(firstResponse, endsWith("\r\n"));

        sendRequestToServer(
                "GET / HTTP/1.1\r\n" +
                        "Accept-Encoding: gzip\r\n" +
                        "User-Agent: Jetty/9.3.12.v20160915\r\n" +
                        "Host: localhost:8080\r\n" +
                        "\r\n"
        );

        waitForResponse();

        String secondResponse = readResponseFromServer();

        assertThat(secondResponse, startsWith("HTTP/1.1 200 OK\r\n"));
        assertThat(secondResponse, containsString("Host: localhost\r\n"));
        assertThat(secondResponse, containsString("Content-Length: 0\r\n"));
        assertThat(secondResponse, endsWith("\r\n"));
    }

    @Test
    public void serverSend_settingFrameAfterGetMagicPriRequest() throws Exception {
        sendRequestToServer(
                "PRI * HTTP/2.0\r\n" +
                        "\r\n" +
                        "SM\r\n" +
                        "\r\n"
        );

        waitForResponse();

        byte[] response = readResponseAsByteArray();

        assertThat(decodePayloadLength(response), is(0));
        assertThat(decodeFrameType(response), is(4));
        assertThat(decodeFlags(response), is(1));
        assertThat(decodeStreamIdentifier(response), is(0));
    }

    private int decodePayloadLength(byte[] response) {
        return response[0] << 16 | response[1] << 8 | response[2];
    }

    private int decodeFrameType(byte[] response) {
        return response[3];
    }

    private int decodeFlags(byte[] response) {
        return response[4];
    }

    private int decodeStreamIdentifier(byte[] response) {
        return 0x7F_FF_FF_FF & (response[5] << 24 | response[6] << 16 | response[7] << 8 | response[8]);
    }

    @Test(timeout = 7_500L)
    public void serverSend_headerFrameWith_status200_onHeaderFrame_toRoot() throws Exception {
        int maxHeaderSize = 4096;
        int maxHeaderTableSize = 4096;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(0);out.write(0);out.write(38);
        out.write(1);out.write(5);
        out.write(0);out.write(0);out.write(0);out.write(1);

        Encoder encoder = new Encoder(maxHeaderTableSize);
        encoder.encodeHeader(out, ":scheme".getBytes(), "http".getBytes(), false);
        encoder.encodeHeader(out, ":method".getBytes(), "GET".getBytes(), false);
        encoder.encodeHeader(out, ":authority".getBytes(), "localhost:8080".getBytes(), false);
        encoder.encodeHeader(out, ":path".getBytes(), "/".getBytes(), false);
        encoder.encodeHeader(out, "accept-encoding".getBytes(), "gzip".getBytes(), false);
        encoder.encodeHeader(out, "user-agent".getBytes(), "Jetty/9.3.12.v20160915".getBytes(), false);

        sendRequestToServer(out.toByteArray());

        waitForResponse();

        byte[] response = readResponseAsByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(response);

        int length = in.read() << 16 | in.read() << 8 | in.read();
        assumeThat(length, is(1));
        int type = in.read();
        assertThat(type, is(1));
        int flags = in.read();
        assertThat(flags, is(5)); //end of stream and end of headers
        int stream = 0x7F_FF_FF_FF & (in.read() << 24 | in.read() << 16 | in.read() << 8 | in.read());
        assertThat(stream, is(1));
        Decoder decoder = new Decoder(maxHeaderSize, maxHeaderTableSize);
        Map<String, String> headers = new HashMap<>();
        decoder.decode(in, (name, value, sensitive) -> headers.put(new String(name), new String(value)));
        decoder.endHeaderBlock();

        assertThat(headers, hasEntry(":status", "200"));
    }
}
