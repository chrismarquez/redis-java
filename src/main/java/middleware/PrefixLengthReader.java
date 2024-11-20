package middleware;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PrefixLengthReader {

    private final InputStream stream;

    public PrefixLengthReader(InputStream inputStream) {
        this.stream = inputStream;
    }

    private String parseLengthPrefixedString(int prefix) throws IOException {
        final int length = extractNextLength(prefix);
        final var rawValue = stream.readNBytes(length);
        return new String(rawValue);
    }

    private String parseLengthPrefixedInteger(int prefix) throws IOException {
        final int integerType = prefix & 0b00111111;
        switch (integerType) {
            case 0 -> {
                int value = stream.read();
                return Integer.toString(value);
            }
            case 1 -> {
                ByteBuffer buffer = ByteBuffer.wrap(stream.readNBytes(2));
                int value = buffer.getInt();
                return Integer.toString(value);
            }
            case 2 -> {
                ByteBuffer buffer = ByteBuffer.wrap(stream.readNBytes(4));
                int value = buffer.getInt();
                return Integer.toString(value);
            }
        }
        return "";
    }

    public int extractNextLength() throws IOException {
        final var prefix = stream.read();
        return extractNextLength(prefix);
    }

    public int extractNextLength(int prefix) throws IOException {
        return prefix & 0b00111111;
    }

    public long extractNextMillisTimestamp() throws IOException {
        final var rawTimestamp = stream.readNBytes(8);
        ByteBuffer buffer = ByteBuffer.wrap(rawTimestamp);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return buffer.getLong();
    }

    public String extractNext(int prefix) throws IOException {
        final int preamble = prefix >> 6;
        return switch (preamble) {
            case 0 -> parseLengthPrefixedString(prefix);
            case 3 -> parseLengthPrefixedInteger(prefix);
            default -> "";
        };
    }

    public String extractNext() throws IOException {
        final int prefix = stream.read();
        return extractNext(prefix);
    }

}
