package ua.http.ws;

import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RequestHandlerTest {

    @Test
    public void handleGetRootRequest() throws Exception {
        RequestHandler requestHandler = new RequestHandler();
        Response response = requestHandler.handle(new Request(Method.GET, "/", Version.HTTP_1_1, Collections.singletonMap("Host", "server.host.name")));

        assertThat(response.httpVersion, is(Version.HTTP_1_1));
        assertThat(response.status, is(200));
        assertThat(response.humanRead, is("OK"));
        assertThat(response.headers, is(Collections.singletonMap("Host", "localhost")));
    }
}
