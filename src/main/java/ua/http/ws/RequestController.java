package ua.http.ws;

public class RequestController {
    public Response handleRequest(Request request) {
        return new Response(request.getHttpVersion());
    }
}
