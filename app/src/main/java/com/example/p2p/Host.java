package com.example.p2p;

import java.net.InetAddress;

public final class Host {
    public final static int defaultOffset = 10;
    public final static int defaultPort = 4444;
    public final int port;
    public final InetAddress ip;
    public final int portOffset;

    public Host(InetAddress ip, int port, int portOffset) {
        this.ip = ip;
        this.port = port;
        this.portOffset = portOffset;
    }

    public Host(InetAddress ip) {
        this(ip, defaultPort, defaultOffset);
    }

    public Host(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
    }
}
