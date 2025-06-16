package service;

import config.Config;
import config.Flags.ReplicaOf;
import middleware.RedisProtocolReader;
import middleware.RedisProtocolWriter;
import model.Responses.ArrayResponse;
import model.Responses.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;
import java.util.Optional;

public class ReplicationService {

    private final Config config;
    private final String role;

    private final String replicationID = "8371b4fb1155b71f4a04d3e1bc3e18c4a990aeeb";
    private final int replicationOffset = 0;

    private final Optional<ReplicaOf> masterInfo;

    public ReplicationService(Config config) {
        this.config = config;
        this.role = this.config.hasConfig("replicaof") ? "slave" : "master";
        this.masterInfo = this.config.getConfig("replicaof")
            .map(ReplicaOf::new);
    }

    public void doHandshake() throws IOException {
        if (this.masterInfo.isEmpty()) {
            return;
        }
        final var info = masterInfo.get();
        final var address = InetAddress.getByName(info.masterHost());
        try (final var socket = new Socket(address, info.masterPort())) {
            var reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            var writer = new PrintWriter(socket.getOutputStream());
            var protocolReader = new RedisProtocolReader(reader);
            var protocolWriter = new RedisProtocolWriter(writer);
            protocolWriter.writeEncoded(getPing());
            var _ = protocolReader.getNextOK();
            protocolWriter.writeEncoded(getListeningPortConfig());
            var _ = protocolReader.getNextOK();
            protocolWriter.writeEncoded(getCapabilitiesConfig());
            var _ = protocolReader.getNextOK();
            protocolWriter.writeEncoded(getSync());
            var _ = protocolReader.getNextOK();
        }
    }

    private Response getPing() {
        return ArrayResponse.from("PING");
    }

    private Response getListeningPortConfig() {
        final var port = this.config.getConfig("port")
            .orElse("0");
        return getReplicaConfig("listening-port", port);
    }

    private Response getCapabilitiesConfig() {
        return getReplicaConfig("capa", "psync2");
    }

    private Response getReplicaConfig(String key, String value) {
        return ArrayResponse.from("REPLCONF", key, value);
    }

    private Response getSync() {
        return ArrayResponse.from("PSYNC", "?", "-1");
    }

    public Map<String, String> getReplicationInfo() {
        return Map.of(
            "role", this.role,
            "master_replid", this.replicationID,
            "master_repl_offset", Integer.toString(this.replicationOffset)
        );
    }

}
