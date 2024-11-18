import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    private static void sendPong(PrintWriter writer) {
        final var message = "+PONG\r\n";
        writer.write(message);
        writer.flush();
    }

    private static void handleConnection(Socket clientSocket) {
        try {
            var reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            var writer = new PrintWriter(clientSocket.getOutputStream());
            String input;
            while ((input = reader.readLine()) != null) {
                System.out.println(input);
                if (input.toLowerCase().startsWith("ping")) {
                    sendPong(writer);
                }
            }
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

    private static void acceptConnection(ServerSocket serverSocket) {
        try {
            var clientSocket = serverSocket.accept();
            Thread thread = new Thread(() -> handleConnection(clientSocket));
            thread.start();
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
