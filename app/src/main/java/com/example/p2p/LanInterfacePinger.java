package com.example.p2p;

import java.net.InetAddress;
import java.util.concurrent.CompletableFuture;

public interface LanInterfacePinger {
    void send(Host possilbeHost, byte[] data);

    void close();
}
