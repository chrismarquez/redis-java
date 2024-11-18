import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    private static void sendPong(Socket clientSocket) {
        final var message = "+PONG\r\n";
        try (var stream = clientSocket.getOutputStream()) {
            var writer = new PrintWriter(stream);
            writer.write(message);
            writer.flush();
        } catch (IOException e) {
            System.out.println("Shit");
        }
    }

    private static void acceptConnection(ServerSocket serverSocket) {
        try (var clientSocket = serverSocket.accept()) {
            sendPong(clientSocket);
        } catch (IOException e) {
            System.out.println("Client socket error");
        }
    }

    private static ServerSocket initServer(int port) throws IOException {
        var serverSocket = new ServerSocket(port);
        serverSocket.setReuseAddress(true);
        return serverSocket;
    }

    private static void listen(int port) {
        try (var serverSocket = initServer(port)) {
            System.out.println("Listening on port " + port);
            while (true) {
                acceptConnection(serverSocket);
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println("Logs from your program will appear here!");
        int port = 6379;
        listen(port);
    }
}
