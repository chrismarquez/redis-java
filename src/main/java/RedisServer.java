import config.Config;
import controller.RedisController;
import service.RedisService;

import java.io.*;
import java.net.ServerSocket;

public class RedisServer {

    private final RedisEventLoop eventLoop;
    private final Config config;

    public RedisServer(Config config) {
        this.config = config;

        var service = new RedisService(config);
        var controller = new RedisController(config, service);
        this.eventLoop = new RedisEventLoop(config, controller);
    }

    private void acceptConnection(ServerSocket serverSocket) {
        try {
            var clientSocket = serverSocket.accept();
            eventLoop.addConnection(clientSocket);
        } catch (IOException e) {
            System.out.println("Client socket error");
        }
    }

    private ServerSocket initServer(int port) throws IOException {
        var serverSocket = new ServerSocket(port);
        serverSocket.setReuseAddress(true);
        return serverSocket;
    }

    private void listen(int port) {
        try (var serverSocket = initServer(port)) {
            System.out.println("Listening on port " + port);
            while (!serverSocket.isClosed()) {
                acceptConnection(serverSocket);
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Config config = new Config(args);
        RedisServer server = new RedisServer(config);
        int port = 6379;
        server.listen(port);
    }
}
