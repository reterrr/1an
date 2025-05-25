package com.example.p2p;

import org.json.JSONObject;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public final class PingDataBinaryConverter {
    public static byte[] convert(PingData data) {
        String prefix = String.format(
                "%s::%d::",
                data.localServerAddress.getHostAddress(),
                data.localServerPort
        );

        String fullMessage = prefix + data.json.toString();
        return fullMessage.getBytes(StandardCharsets.UTF_8);
    }

    public static PingData convert(byte[] data) {
        String s = new String(data, StandardCharsets.UTF_8);

        int firstSep = s.indexOf("::");
        int secondSep = s.indexOf("::", firstSep + 2);

        if (firstSep == -1 || secondSep == -1) {
            throw new IllegalArgumentException("Invalid PingData format");
        }

        String addressStr = s.substring(0, firstSep);
        String portStr = s.substring(firstSep + 2, secondSep);
        String jsonStr = s.substring(secondSep + 2);

        try {
            InetAddress serverAddress = InetAddress.getByName(addressStr);
            int serverPort = Integer.parseInt(portStr);

            return new PingData("1",serverAddress, serverPort, new JSONObject(jsonStr));
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse PingData: " + e.getMessage());
        }
    }

}
