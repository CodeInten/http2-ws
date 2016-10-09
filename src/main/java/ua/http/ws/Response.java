package ua.http.ws;

public class Response {
    private final Version httpVersion;

    public Response(Version httpVersion) {
        this.httpVersion = httpVersion;
    }

    public Version getHttpVersion() {
        return httpVersion;
    }
}
