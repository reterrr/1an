package com.example.p2p;

import java.net.InetAddress;
import java.util.concurrent.CompletableFuture;

public interface LanInterfacePinger {
    CompletableFuture<Peer> pingPeer(InetAddress peerIp, String userName);

    void close();
}
