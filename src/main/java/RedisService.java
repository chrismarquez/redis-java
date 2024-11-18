import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RedisService {

    private final Map<String, String> values = new HashMap<>();

    public RedisService() {}

    public Optional<String> getValue(String key) {
        if (!values.containsKey(key)) return Optional.empty();
        return Optional.of(values.get(key));
    }

    public void setValue(String key, String value) {
        values.put(key, value);
    }


}
