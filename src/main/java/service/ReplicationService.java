package service;

import config.Config;

import java.util.Map;

public class ReplicationService {

    private final Config config;
    private final String role;

    public ReplicationService(Config config) {
        this.config = config;
        this.role = getRole();
    }

    private String getRole() {
        return this.config.hasConfig("replicaof") ? "master" : "slave";
    }

    public Map<String, String> getReplicationInfo() {
        return Map.of(
            "role", this.role
        );
    }

}
