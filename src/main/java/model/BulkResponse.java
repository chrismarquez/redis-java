package model;

import java.util.Optional;

public record BulkResponse(Optional<String> payload) implements Response {}
