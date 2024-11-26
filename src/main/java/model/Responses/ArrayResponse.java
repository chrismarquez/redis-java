package model.Responses;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public record ArrayResponse(List<? extends Response> payload) implements Response {
    public static ArrayResponse from(String... list) {
        final var responses = Arrays.stream(list)
            .map(Optional::of)
            .map(BulkResponse::new)
            .toList();
        return new ArrayResponse(responses);
    }
}
