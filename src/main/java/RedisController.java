import java.util.Optional;

public class RedisController {

    private final RedisService redisService = new RedisService();

    public RedisController() {}

    private Response handlePing(Command command) {
        return new Response(Optional.of("PONG"), Response.Type.Simple);
    }

    private Response handleEcho(Command command) {
        var echo =  command.args().getFirst();
        return new Response(Optional.of(echo), Response.Type.Bulk);
    }

    private Response handleGet(Command command) {
        var key = command.args().getFirst();
        var value = redisService.getValue(key);
        return new Response(value, Response.Type.Bulk);
    }

    private Response handleSet(Command command) {
        var key = command.args().getFirst();
        var value = command.args().getLast();
        redisService.setValue(key, value);
        return new Response(Optional.of("OK"), Response.Type.Simple);
    }

    public Response process(Command command) {
        System.out.println(command);
        return switch (command.name()) {
            case PING -> this.handlePing(command);
            case ECHO -> this.handleEcho(command);
            case GET -> this.handleGet(command);
            case SET -> this.handleSet(command);
            case UNKNOWN -> new Response(Optional.empty(), Response.Type.Simple);
        };
    }

}
