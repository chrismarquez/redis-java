package config;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Config {

    private final Map<String, String> configMap;

    public Config(String[] args) {
        final var count = new AtomicInteger();
        var params = Arrays.stream(args)
            .collect(Collectors.groupingBy(_ -> {
                final var i = count.getAndIncrement();
                return (i % 2 == 0) ? i : (i - 1);
            }));
        this.configMap = params.values().stream()
            .filter(values -> values.size() == 2)
            .collect(Collectors.toMap(values -> values.getFirst().replace("--", ""), List::getLast));
        System.out.println("Retrieving Server config...");
        System.out.println(this.configMap);
    }

    public boolean hasConfig(String key) {
        return this.configMap.containsKey(key);
    }

    public Optional<String> getConfig(String key) {
        return Optional.ofNullable(this.configMap.get(key));
    }

}
