package service;

import config.Config;

import java.util.Map;

public class ReplicationService {

    private final Config config;
    private final String role;

    private final String replicationID = "8371b4fb1155b71f4a04d3e1bc3e18c4a990aeeb";
    private final int replicationOffset = 0;

    public ReplicationService(Config config) {
        this.config = config;
        this.role = getRole();
    }

    private String getRole() {
        return this.config.hasConfig("replicaof") ? "slave" : "master";
    }

    public Map<String, String> getReplicationInfo() {
        return Map.of(
            "role", this.role,
            "master_replid", this.replicationID,
            "master_repl_offset", Integer.toString(this.replicationOffset)
        );
    }

}
