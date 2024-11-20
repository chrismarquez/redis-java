package model;

import middleware.PrefixLengthReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class RDBFile {

    private final InputStream stream;

    private static final int REDIS_MAGIC_WORD_LEN = 5;
    private static final int REDIS_VERSION_LEN = 4;
    private int fileVersion;
    private Map<String, String> auxiliaryFields = new HashMap<>();
    private Map<Integer, Map<String, String>> databases = new HashMap<>();

    private PrefixLengthReader prefixReader;


    private int currentDatabase = -1;

    public RDBFile(InputStream stream) {
        this.stream = stream;
        this.prefixReader = new PrefixLengthReader(stream);
        this.fileVersion = 0;
    }

    private void extractHeader() throws IOException {
        final var rawRedisMagic = this.stream.readNBytes(REDIS_MAGIC_WORD_LEN);
        final var redisMagic = new String(rawRedisMagic);
        assert redisMagic.equals("REDIS");
        final var rawRDBVersion = this.stream.readNBytes(REDIS_VERSION_LEN);
        fileVersion = Integer.parseInt(new String(rawRDBVersion));
    }

    private void extractAuxiliaryField() throws IOException {
        final var key = prefixReader.extractNext();
        final var value = prefixReader.extractNext();
        auxiliaryFields.put(key, value);
        System.out.println(auxiliaryFields);
    }

    private void extractDBSelector() throws IOException {
        this.currentDatabase = prefixReader.extractNextLength();
        this.databases.put(currentDatabase, new HashMap<>());
    }

    private void extractResizeDBField() throws IOException {
        var hashTableSize = prefixReader.extractNextLength();
        var expiryHashTableSize = prefixReader.extractNextLength();
        return;
    }

    private void extractExpirableInSecondsPair() {

    }

    private void extractExpirableInMSPair() throws IOException {
        final var timestamp = prefixReader.extractNextMillisTimestamp();
        final var valueType = stream.read();
        extractPair(valueType);
    }

    private void extractEndOfFile() throws IOException {
        final var checksum = stream.readNBytes(8);
        return;
    }

    private void extractPair(int valueType) throws IOException {
        final var key = prefixReader.extractNext();
        final var value = prefixReader.extractNext();
        final var database = databases.get(currentDatabase);
        database.put(key, value);
    }


    public Map<Integer, Map<String, String>> parse() {
        int token;
        try {
            extractHeader();
            mainLoop: while ((token = stream.read()) != -1) {
                switch (token) {
                    case 0xFA -> extractAuxiliaryField();
                    case 0xFB -> extractResizeDBField();
                    case 0xFC -> extractExpirableInMSPair();
                    case 0xFD -> extractExpirableInSecondsPair();
                    case 0xFE -> extractDBSelector();
                    case 0xFF -> {
                        extractEndOfFile();
                        break mainLoop;
                    }
                    default -> extractPair(token);
                }
            }
            System.out.println("Finished parsing");
            System.out.println("DB Contents: ");
            for (var db: databases.values()) {
                System.out.println(db);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return databases;
    }

}
