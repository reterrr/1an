package com.example.p2p;

public class ServerInfo {
    private static int serverPort;

    public synchronized static int getServerPort() {
        return serverPort;
    }

    public synchronized static void setServerPort(int serverPort) {
        ServerInfo.serverPort = serverPort;
    }
}
