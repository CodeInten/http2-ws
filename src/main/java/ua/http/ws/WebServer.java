package ua.http.ws;

import java.io.IOException;
import java.net.ServerSocket;

public class WebServer {
    private int port;
    private ServerSocket serverSocket;

    public WebServer(String[] params) {
        for (int i = 0; i < params.length; i++) {
            if ("-p".equals(params[i])) {
                port = Integer.parseInt(params[i + 1]);
                i += 1;
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new WebServer(args).start();
    }

    public void start() throws IOException, InterruptedException {
        serverSocket = new ServerSocket(port);
        new Thread(() -> {
            try {
                serverSocket.accept();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void stop() throws IOException {
        serverSocket.close();
    }
}
