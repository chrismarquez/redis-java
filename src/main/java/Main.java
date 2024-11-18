import java.io.*;
import java.net.ServerSocket;

public class Main {

    private static void sendPong(PrintWriter writer) {
        final var message = "+PONG\r\n";
        writer.write(message);
        writer.flush();
    }

    private static void acceptConnection(ServerSocket serverSocket) {
        try (var clientSocket = serverSocket.accept()) {
            var reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            var writer = new PrintWriter(clientSocket.getOutputStream());
            String input;
            while ((input = reader.readLine()) != null) {
                System.out.println(input);
                if (input.toLowerCase().startsWith("ping")) {
                    sendPong(writer);
                }
            }
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
            acceptConnection(serverSocket);
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
