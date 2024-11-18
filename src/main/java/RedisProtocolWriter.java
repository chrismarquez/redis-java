import java.io.PrintWriter;
import java.util.Optional;

public class RedisProtocolWriter {

    private final PrintWriter writer;

    public RedisProtocolWriter(PrintWriter writer) {
        this.writer = writer;
    }

    private String encodeBulk(Optional<String> payload) {
        return payload
            .map((value) -> String.format("$%d\r\n%s\r\n", value.length(), value))
            .orElse("$-1\r\n");
    }

    private String encodeSimple(Optional<String> payload) {
        return payload
            .map((value) -> String.format("+%s\r\n", value))
            .orElse("_\r\n");
    }

    public void writeEncoded(Response response) {
        var encodedResponse = switch (response.type()) {
            case Simple -> encodeSimple(response.payload());
            case Bulk -> encodeBulk(response.payload());
        };
        writer.write(encodedResponse);
        writer.flush();
    }

}
