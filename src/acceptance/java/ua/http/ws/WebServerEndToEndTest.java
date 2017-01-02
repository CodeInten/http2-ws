package ua.http.ws;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http2.client.HTTP2Client;
import org.eclipse.jetty.http2.client.http.HttpClientTransportOverHTTP2;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(HierarchicalContextRunner.class)
public class WebServerEndToEndTest {
    private Process webServer;
    private HttpClient client;

    @Before
    public void startWebServer() throws IOException, InterruptedException {
        webServer = new ProcessBuilder("java", "-cp", "build/classes/main"+ File.pathSeparator+"build/runtime/*", "ua.http.ws.WebServer", "-p", "8080")
                .start();
        webServer.waitFor(1, TimeUnit.SECONDS);
    }

    @After
    public void stopWebServer() throws Exception {
//        if (webServer.exitValue() != 0) {
//            printWebServerErrors();
//        }
        webServer.destroy();
    }

    private void printWebServerErrors() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(webServer.getErrorStream()));
        String line = null;
        while ((line = reader.readLine()) != null) {
            System.out.println (line);
        }
    }

    public class SystemTests {

        @Test
        public void serverShouldListenToTheSpecifiedPort() throws Exception {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(InetAddress.getLocalHost(), 8080));

            assertThat(socket.isConnected(), is(true));
        }

    }

    public class HttpTests {

        @Before
        public void startWebClient() throws Exception {
            client = new HttpClient();
        }

        @After
        public void stopClient() throws Exception {
            client.stop();
        }

        @Test(timeout = 7_500)
        public void serverShouldSendOk_whenGetRequestOnRoot() throws Exception {
            client.start();

            ContentResponse response = sendRequestOnRootUrl();

            assertThat(response.getStatus(), is(HttpStatus.Code.OK.getCode()));
            assertThat(response.getVersion(), is(HttpVersion.HTTP_1_1));
        }

        @Test(timeout = 7_500)
        public void serverShouldSend_200_onTheSecondRequestOnRoot() throws Exception {
            client.start();

            sendRequestOnRootUrl();

            ContentResponse response = sendRequestOnRootUrl();

            assertThat(response.getStatus(), is(HttpStatus.Code.OK.getCode()));
            assertThat(response.getVersion(), is(HttpVersion.HTTP_1_1));
        }

        private ContentResponse sendRequestOnRootUrl() throws InterruptedException, TimeoutException, ExecutionException {
            return client.newRequest("http://localhost:8080/")
                    .method(HttpMethod.GET)
                    .timeout(10, TimeUnit.SECONDS)
                    .version(HttpVersion.HTTP_1_1)
                    .send();
        }

        @Test(timeout = 7_500)
        public void serverShouldSendOk_whenClientAskForConnectionUpdateToHttp_2() throws Exception {
            client.start();

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

        @Test(timeout = 27_500)
        public void serverShouldSendOk_whenClientRequestRoot_byHttp2() throws Exception {
            HTTP2Client lowLevelClient = new HTTP2Client();
            lowLevelClient.start();

            client = new HttpClient(new HttpClientTransportOverHTTP2(lowLevelClient), null);
            client.start();

            ContentResponse response = client.newRequest("http://localhost:8080/")
                    .method(HttpMethod.GET)
                    .timeout(10, TimeUnit.SECONDS)
                    .version(HttpVersion.HTTP_2)
                    .send();

            assertThat(response.getStatus(), is(HttpStatus.Code.OK.getCode()));
            assertThat(response.getVersion(), is(HttpVersion.HTTP_2));
        }
    }
}
