import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class RedisServer {

    private final RedisController controller = new RedisController();

    public RedisServer() {}

    private void sendResponse(String message, PrintWriter writer) {
        writer.write(message);
        writer.flush();
    }

    private void handleCommands(RedisProtocolReader parser, PrintWriter writer) throws IOException {
        Command command;
        while((command = parser.getNextCommand()) != null) {
            String response = controller.process(command);
            sendResponse(response, writer);
        }
    }

    private void handleConnection(Socket clientSocket) {
        try {
            var reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            var writer = new PrintWriter(clientSocket.getOutputStream());
            var parser = new RedisProtocolReader(reader);
            handleCommands(parser, writer);
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
