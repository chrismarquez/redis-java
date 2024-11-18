import java.util.Optional;

public record Response(Optional<String> payload, Type type) {

    public enum Type {
        Simple,
        Bulk
    }

}
