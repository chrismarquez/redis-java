package model.Responses;

import java.util.List;

public record ArrayResponse(List<Response> payload) implements Response {}
