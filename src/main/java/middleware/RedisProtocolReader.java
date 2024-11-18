package middleware;

import model.Command;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import java.util.List;

public class RedisProtocolReader {

    private final BufferedReader reader;

    public RedisProtocolReader(BufferedReader reader) {
        this.reader = reader;
    }

    private int getArgsCount(String starCommand) {
        var rawCount = starCommand.replace("*", "");
        return Integer.parseInt(rawCount);
    }

    public Command getNextCommand() throws IOException {
        String line = reader.readLine();
        if (line == null) {
            return null;
        }
        var argsCount = getArgsCount(line);
        List<String> args = new ArrayList<>();
        var _ = reader.readLine();
        var commandName = reader.readLine().toUpperCase();
        for (int i = 1; i < argsCount; i++) {
            var _ = reader.readLine();
            args.add(reader.readLine());
        }
        return new Command(commandName, args);
    }

}
