import config.Config;
import controller.RedisController;
import middleware.RedisProtocolReader;
import middleware.RedisProtocolWriter;
import model.Command;
import model.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.*;

public class RedisEventLoop {

    private final ExecutorService executor = new ThreadPoolExecutor(
        0,
        1_000,
        60L, TimeUnit.SECONDS,
        new SynchronousQueue<>(),
        Executors.defaultThreadFactory()
    );
    private final Config config;
    private final RedisController controller;

    public RedisEventLoop(Config config, RedisController controller) {
        this.config = config;
        this.controller = controller;
    }

    public void addConnection(Socket clientSocket) {
        executor.submit(() -> handleConnection(clientSocket));
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

    private void handleCommands(RedisProtocolReader reader, RedisProtocolWriter writer) throws IOException {
        Command command;
        while((command = reader.getNextCommand()) != null) {
            Response response = controller.process(command);
            writer.writeEncoded(response);
        }
    }

}
