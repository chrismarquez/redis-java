import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class RedisController {

    private final Map<Command.Name, Function<Command, String>> commandMapper = new HashMap<>();

    public RedisController() {
        commandMapper.put(Command.Name.PING, this::handlePing);
        commandMapper.put(Command.Name.ECHO, this::handleEcho);
    }

    private String handlePing(Command command) {
        return "+PONG\r\n";
    }

    private String handleEcho(Command command) {
        var echo = command.args().getFirst();
        return String.format("$%d\r\n%s\r\n", echo.length(), echo);
    }

    public String process(Command command) {
        System.out.println(command);
        var commandHandler = commandMapper.getOrDefault(command.name(), (_) -> "");
        return commandHandler.apply(command);
    }

}
