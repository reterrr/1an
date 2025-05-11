package com.example.p2p;


import java.net.InetAddress;

public final class Peer {
    public Peer(InetAddress ip, int port, String userName) {
        this.ip = ip;
        this.userName = userName;
        this.port = port;
    }

    public final String userName;
    public final InetAddress ip;

    public final int port;
}
