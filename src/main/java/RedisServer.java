import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class RedisServer {

    private final RedisController controller = new RedisController();

    public RedisServer() {}

    private void handleCommands(RedisProtocolReader reader, RedisProtocolWriter writer) throws IOException {
        Command command;
        while((command = reader.getNextCommand()) != null) {
            Response response = controller.process(command);
            writer.writeEncoded(response);
        }
    }

    private void handleConnection(Socket clientSocket) {
        try {
            var reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            var writer = new PrintWriter(clientSocket.getOutputStream());
            var protocolReader = new RedisProtocolReader(reader);
            var protocolWriter = new RedisProtocolWriter(writer);
            handleCommands(protocolReader, protocolWriter);
        }  catch (IOException e) {
            System.out.println("Client socket error");
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Closing socket error");
            }
        }
    }

    private void acceptConnection(ServerSocket serverSocket) {
        try {
            var clientSocket = serverSocket.accept();
            Thread thread = new Thread(() -> handleConnection(clientSocket));
            thread.start();
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
        RedisServer server = new RedisServer();
        int port = 6379;
        server.listen(port);
    }
}
