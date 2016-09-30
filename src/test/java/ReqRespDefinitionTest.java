import org.junit.Test;

public class ReqRespDefinitionTest {
    @Test
    public void theWay_howISee_veryBasicVersionOfRequest() throws Exception {
        new Request() {
            public String body() { return ""; }
            public String contentType() { return ""; }
            public int contentLength() { return 0; }
            public String queryString() { return ""; }

            public String protocol() { return ""; }
            public String host() { return ""; }
            public int port() { return 0; }
        };
    }
}
