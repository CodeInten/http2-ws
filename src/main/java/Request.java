public interface Request {
    String body();

    String contentType();

    int contentLength();

    String queryString();

    String protocol();

    String host();

    int port();
}
