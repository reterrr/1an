package com.example.p2p;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public final class PingDataBinaryConverter {
    public static byte[] convert(PingData data) {
        return String.format(
                        "%s::%s::%d",
                        data.message,
                        data.localServerAddress.getHostAddress(),
                        data.localServerPort
                )
                .getBytes(StandardCharsets.UTF_8);
    }

    public static PingData convert(byte[] data) {
        String s = new String(data, StandardCharsets.UTF_8);
        String[] parts = s.split("::");

        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid PingData format");
        }

        String message = parts[0];
        String address = parts[1];
        String portStr = parts[2];

        try {
            InetAddress serverAddress = InetAddress.getByName(address);
            int serverPort = Integer.parseInt(portStr);
            return new PingData(message, serverAddress, serverPort);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse PingData: " + e.getMessage());
        }
    }
}
