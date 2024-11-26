import config.Config;
import controller.RedisController;
import model.RDBFile;
import service.ReplicationService;
import service.StorageService;

import java.io.*;
import java.net.ServerSocket;

public class RedisServer {

    private final RedisEventLoop eventLoop;
    private final Config config;

    public RedisServer(Config config) {
        this.config = config;
        var storageService = new StorageService(config);
        var replicationService = new ReplicationService(config);
        var controller = new RedisController(config, storageService, replicationService);
        this.eventLoop = new RedisEventLoop(config, controller);
        loadStoredDatabases(config, storageService);
    }

    private void loadStoredDatabases(Config config, StorageService service){
        final var directory = config.getConfig("dir").orElse("");
        final var fileName = config.getConfig("dbfilename").orElse("");
        final var path = directory + "/" + fileName;
        try (final var stream = new FileInputStream(path)) {
            RDBFile file = new RDBFile(stream);
            final var databases = file.parse();
            service.bulkLoad(databases);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        int port = config.getConfig("port")
            .map(Integer::parseInt)
            .orElse(6379);
        RedisServer server = new RedisServer(config);
        server.listen(port);
    }
}
