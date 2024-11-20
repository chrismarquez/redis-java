package middleware;

import model.Responses.ArrayResponse;
import model.Responses.BulkResponse;
import model.Responses.Response;
import model.Responses.SimpleResponse;

import java.io.PrintWriter;
import java.util.stream.Collectors;


public class RedisProtocolWriter {

    private final PrintWriter writer;

    public RedisProtocolWriter(PrintWriter writer) {
        this.writer = writer;
    }

    private String encodeArray(ArrayResponse response) {
        final var encodedItems = response.payload().stream()
            .map(this::encode)
            .collect(Collectors.joining());
        return String.format("*%d\r\n%s", response.payload().size(), encodedItems);
    }

    private String encodeBulk(BulkResponse response) {
        return response.payload()
            .map((value) -> String.format("$%d\r\n%s\r\n", value.length(), value))
            .orElse("$-1\r\n");
    }

    private String encodeSimple(SimpleResponse response) {
        return String.format("+%s\r\n", response.payload());
    }

    private String encode(Response response) {
        return switch (response) {
            case SimpleResponse simple -> this.encodeSimple(simple);
            case BulkResponse bulk -> this.encodeBulk(bulk);
            case ArrayResponse array -> this.encodeArray(array);
        };
    }

    public void writeEncoded(Response response) {
        final var encodedResponse = this.encode(response);
        writer.write(encodedResponse);
        writer.flush();
    }

}
