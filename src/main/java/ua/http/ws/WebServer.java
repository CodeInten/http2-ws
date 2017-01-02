package ua.http.ws;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {
    private Socket clientConnection;
    private ServerSocket serverSocket;

    public WebServer(String[] params) {}

    public static void main(String[] args) throws IOException, InterruptedException {
        new WebServer(args).start();
    }

    public void start() throws IOException, InterruptedException {
        serverSocket = new ServerSocket(8080);
        new Thread(() -> {
            try {
                clientConnection = serverSocket.accept();
                while (true) {
                    RequestReader reader = new RequestReader(clientConnection.getInputStream());
                    RequestDeserializer deserializer = new RequestDeserializer(reader.readSingleRequest());
                    Request request = deserializer.deserializeRequest();
                    RequestHandler handler = new RequestHandler();
                    Response response = handler.handle(request);
                    ResponseWriter writer = new ResponseWriter(clientConnection.getOutputStream());
                    ResponseSerializer serializer = new ResponseSerializer();
                    writer.writeSingleResponse(serializer.serialize(response));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void stop() throws IOException {
        serverSocket.close();
    }
}
