package com.example.p2p;

import java.net.InetAddress;

public final class Peer {
    public final String userName;
    public final InetAddress ip;
    public final int port;

    public Peer(String userName, InetAddress ip, int port) {
        this.userName = userName;
        this.ip = ip;
        this.port = port;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Peer)) return false;
        Peer u = (Peer) obj;

        return ip.equals(u.ip) && port == u.port && userName.equals(u.userName);
    }

    @Override
    public int hashCode() {
        return ip.hashCode() + port + userName.hashCode();
    }
}
