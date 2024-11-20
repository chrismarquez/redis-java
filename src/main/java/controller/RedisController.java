package controller;

import config.Config;
import model.*;
import model.Responses.ArrayResponse;
import model.Responses.BulkResponse;
import model.Responses.Response;
import model.Responses.SimpleResponse;
import service.RedisService;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class RedisController {

    private final RedisService redisService;
    private final Config config;

    public RedisController(Config config, RedisService redisService) {
        this.config = config;
        this.redisService = redisService;
    }

    private Response handlePing(Command command) {
        return new SimpleResponse("PONG");
    }

    private Response handleEcho(Command command) {
        var echo =  command.args().getFirst();
        return new BulkResponse(Optional.of(echo));
    }

    private Response handleGet(Command command) {
        var key = command.args().getFirst();
        var value = redisService.getValue(key);
        return new BulkResponse(value);
    }

    private Response handleSet(Command command) {
        if (command.args().size() == 2) {
            var key = command.args().getFirst();
            var value = command.args().getLast();
            redisService.setValue(key, value);
        } else if (command.args().size() == 4) {
            var key = command.args().getFirst();
            var value = command.args().get(1);
            var expiry = Long.parseLong(command.args().getLast());
            redisService.setValue(key, value, expiry);
        }
        return new SimpleResponse("OK");
    }

    private Response handleKeys(Command command) {
        final var keys = redisService.getKeys();
        final var responses = keys.stream()
            .map(key -> (Response) new BulkResponse(Optional.of(key)))
            .collect(Collectors.toList());
        return new ArrayResponse(responses);
    }

    private Response handleConfig(Command command) {
        var _ = command.args().getFirst();
        final var configKey = command.args().getLast();
        final var configValue = this.config.getConfig(configKey);
        final var keyResponse = new BulkResponse(Optional.of(configKey));
        final var valueResponse = new BulkResponse(configValue);
        return new ArrayResponse(Arrays.asList(keyResponse, valueResponse));
    }

    public Response process(Command command) {
        System.out.println(command);
        return switch (command.name()) {
            case PING -> this.handlePing(command);
            case ECHO -> this.handleEcho(command);
            case GET -> this.handleGet(command);
            case SET -> this.handleSet(command);
            case KEYS -> this.handleKeys(command);
            case CONFIG ->  this.handleConfig(command);
            case UNKNOWN -> new SimpleResponse("");
        };
    }

}
