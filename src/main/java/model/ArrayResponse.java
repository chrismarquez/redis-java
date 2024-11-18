package model;

import java.util.List;

public record ArrayResponse(List<Response> payload) implements Response {}
