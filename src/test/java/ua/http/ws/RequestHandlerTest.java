package ua.http.ws;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;

public class RequestHandlerTest {

    private RequestHandler handler;

    @Before
    public void setUp() throws Exception {
        handler = new RequestHandler();
    }

    @Test
    public void handleGetRootRequest() throws Exception {
        Response response = handler.handle(
                new RequestBuilder()
                        .withMethod(Method.GET)
                        .withUrl("/")
                        .withVersion(Version.HTTP_1_1)
                        .withHeader("Host", "client.host.name")
                        .build()
        );

        assertThat(response.httpVersion, is(Version.HTTP_1_1));
        assertThat(response.status, is(200));
        assertThat(response.humanRead, is("OK"));
        assertThat(response.headers, hasEntry("Host", "localhost"));
        assertThat(response.headers, hasEntry("Content-Length", "0"));
    }

    @Test
    public void handleUpgradeConnectionRequest() throws Exception {
        Response response = handler.handle(
                new RequestBuilder()
                        .withMethod(Method.GET)
                        .withUrl("/")
                        .withVersion(Version.HTTP_1_1)
                        .withHeader("Connection", "Upgrade")
                        .withHeader("Upgrade", "h2c")
                        .build()
        );

        assertThat(response.httpVersion, is(Version.HTTP_1_1));
        assertThat(response.status, is(101));
        assertThat(response.humanRead, is("Switching Protocols"));
        assertThat(response.headers, hasEntry("Connection", "Upgrade"));
        assertThat(response.headers, hasEntry("Upgrade", "h2c"));
        assertThat(response.headers, hasEntry("Host", "localhost"));
        assertThat(response.headers, hasEntry("Content-Length", "0"));
    }

    @Test
    public void handlePriRequest() throws Exception {
        Response response = handler.handle(
                new RequestBuilder()
                        .withMethod(Method.PRI)
                        .withUrl("*")
                        .withVersion(Version.HTTP_2_0)
                        .withBody("SM")
                        .build()
        );

        assertThat(response, instanceOf(SettingFrame.class));
    }

    @Test
    public void handleHeaderRequest_onRootDirectory() throws Exception {
        Response response = handler.handle(new HeaderFrameRequest());

        assertThat(response, instanceOf(HeaderFrameResponse.class));
    }

    private class RequestBuilder {
        private Map<String, String> headers = new HashMap<>();
        private Method method;
        private String url;
        private Version version;
        private String body;

        RequestBuilder withMethod(Method method) {
            this.method = method;
            return this;
        }

        RequestBuilder withUrl(String url) {
            this.url = url;
            return this;
        }

        RequestBuilder withVersion(Version version) {
            this.version = version;
            return this;
        }

        RequestBuilder withHeader(String header, String value) {
            headers.put(header, value);
            return this;
        }

        public RequestBuilder withBody(String body) {
            this.body = body;
            return this;
        }

        Request build() {
            return new Request(method, url, version, headers, body);
        }
    }
}
