package service;

import config.Config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RedisService {

    private final Map<String, String> values = new HashMap<>();
    private final Config config;

    public RedisService(Config config) {
        this.config = config;
    }

    public Optional<String> getValue(String key) {
        if (!values.containsKey(key)) return Optional.empty();
        return Optional.of(values.get(key));
    }

    public void setValue(String key, String value) {
        values.put(key, value);
    }

    public void setValue(String key, String value, long expiry) {
        values.put(key, value);
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(expiry);
                values.remove(key);
            } catch (InterruptedException _) {
                values.remove(key);
            }

        });
        thread.start();
    }

    public List<String> getKeys() {
        return values.keySet().stream().toList();
    }

}
