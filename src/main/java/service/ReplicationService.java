package service;

import config.Config;
import config.Flags.ReplicaOf;
import middleware.RedisProtocolReader;
import middleware.RedisProtocolWriter;
import model.Responses.ArrayResponse;
import model.Responses.BulkResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ReplicationService {

    private final Config config;
    private final String role;

    private final String replicationID = "8371b4fb1155b71f4a04d3e1bc3e18c4a990aeeb";
    private final int replicationOffset = 0;

    private Optional<ReplicaOf> masterInfo;

    public ReplicationService(Config config) {
        this.config = config;
        this.role = this.config.hasConfig("replicaof") ? "slave" : "master";
        this.masterInfo = this.config.getConfig("replicaof")
            .map(ReplicaOf::new);
    }

    public void sendPing() throws IOException {
        if (this.masterInfo.isEmpty()) {
            return;
        }
        final var info = masterInfo.get();
        final var address = InetAddress.getByName(info.masterHost());
        try (final var socket = new Socket(address, info.masterPort())) {
            var writer = new PrintWriter(socket.getOutputStream());
            var protocolWriter = new RedisProtocolWriter(writer);
            var ping = new BulkResponse(Optional.of("PING"));
            var request = new ArrayResponse(List.of(ping));
            protocolWriter.writeEncoded(request);
        }
    }

    public Map<String, String> getReplicationInfo() {
        return Map.of(
            "role", this.role,
            "master_replid", this.replicationID,
            "master_repl_offset", Integer.toString(this.replicationOffset)
        );
    }

}
