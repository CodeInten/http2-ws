package ua.http.ws;

public class ResponseSerializer {
    public String serialize(Response response) {
        if (response.getHttpVersion() == Version.HTTP_1_1) {
            return "HTTP/1.1 Switching Protocols\n" +
                    "Connection: Upgrade\n" +
                    "Upgrade: h2c";
        }
        else {
            return "HTTP/2 GET 200 OK";
        }
    }
}
