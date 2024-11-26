package config.Flags;

import java.util.Arrays;

public record ReplicaOf(String masterHost, int masterPort) implements ConfigFlag {

    private static String extractHost(String connectionString) {
        final var params = Arrays.asList(connectionString.split("_"));
        return params.getFirst();
    }

    private static int extractPort(String connectionString) {
        final var params = Arrays.asList(connectionString.split("_"));
        return Integer.parseInt(params.getLast());
    }

    public ReplicaOf(String connectionString) {
        this(extractHost(connectionString), extractPort(connectionString));
    }
}
