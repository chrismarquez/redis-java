package model;

import java.util.List;

public record Command(String rawName, List<String> args) {

    public enum Name {
        ECHO,
        PING,
        GET,
        SET,
        CONFIG,
        KEYS,
        UNKNOWN,
    }

    public Name name() {
        try {
            return Name.valueOf(rawName);
        } catch (IllegalArgumentException e) {
            return Name.UNKNOWN;
        }
    }

}
