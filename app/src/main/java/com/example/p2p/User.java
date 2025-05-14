package com.example.p2p;

import java.net.InetAddress;

public final class User {
    public final String userName;
    public final InetAddress ip;
    public final int port;

    public User(InetAddress ip, int port, String userName) {
        this.ip = ip;
        this.userName = userName;
        this.port = port;
    }
}