package service;

import config.Config;
import model.RDBFile;

import java.util.*;
import java.util.concurrent.*;

public class RedisService {

    private final Map<String, String> values = new ConcurrentHashMap<>();
    private final Config config;


    private final ExecutorService executor = new ThreadPoolExecutor(
        0,
        1_000,
        60L,
        TimeUnit.SECONDS,
        new SynchronousQueue<>()
    );

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
        executor.submit(() -> {
            try {
                Thread.sleep(expiry);
                values.remove(key);
            } catch (InterruptedException _) {
                values.remove(key);
            }
        });
    }

    public List<String> getKeys() {
        return values.keySet().stream().toList();
    }

    public void bulkLoad(Map<Integer, RDBFile.Database> databases) {
        final var database = databases.get(0);
        database.values().forEach(this::setValue);
        database.expirableValues().forEach((key, expValue) -> {
            final var currentTimestamp = System.currentTimeMillis();
            final var expiry = expValue.timestamp() - currentTimestamp;
            if (expiry > 0) {
                setValue(key, expValue.value());
            }
        });
    }

}
