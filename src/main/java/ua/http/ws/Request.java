package ua.http.ws;

public class Request {
    private final Version httpVersion;

    public Request(Version httpVersion) {
        this.httpVersion = httpVersion;
    }

    public Version getHttpVersion() {
        return httpVersion;
    }
}
