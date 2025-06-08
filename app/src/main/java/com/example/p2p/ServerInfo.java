package com.example.p2p;

public class ServerInfo {
    private static int serverPort;

    public static int getServerPort() {
        return serverPort;
    }

    public static void setServerPort(int serverPort) {
        ServerInfo.serverPort = serverPort;
    }
}
