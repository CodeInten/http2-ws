package ua.http.ws;

public class RequestDeserializer {
    public Request deserialize(String requestString) {
        if (requestString.contains("HTTP/1.1")) {
            return new Request(Version.HTTP_1_1);
        }
        return new Request(Version.HTTP_2);
    }
}
