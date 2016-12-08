package ua.http.ws;

public class ResponseSerializer {
    public byte[] serialize(Response response) {
        String stringRepresentation = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: 0\r\n" +
                "\r\n";
        byte[] serialized = new byte[stringRepresentation.length()];
        for (int i = 0; i < stringRepresentation.length(); i++) {
            serialized[i] = (byte)stringRepresentation.charAt(i);
        }
        return serialized;
    }
}
