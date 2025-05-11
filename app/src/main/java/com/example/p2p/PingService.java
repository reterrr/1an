package com.example.p2p;

import android.util.Log;

import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
public final class PingService {
    private final LanInterfacePinger pinger;
    private final List<Peer> peers;
    private static final String TAG = "PingService";

    public PingService(LanInterfacePinger pinger, List<Peer> peers) {
        this.pinger = pinger;
        this.peers = (peers instanceof CopyOnWriteArrayList)
                ? peers
                : new CopyOnWriteArrayList<>(peers);
    }

    public void getAlivePeersAsync(Network network, String localUserName) {
        for (InetAddress host : network) {
            pingPeerAsync(host, localUserName)
                    .thenAccept(peer -> {
                        if (peer != null) {
                            synchronized (peers) {
                                if (!peers.contains(peer)) {
                                    peers.add(peer);
                                    Log.i(TAG, "Added peer: " + peer.userName + " at " + peer.ip.getHostAddress());
                                }
                            }
                        }
                    })
                    .exceptionally(throwable -> {
                        Log.w(TAG, "Failed to ping host " + host.getHostAddress() + ": " + throwable.getMessage());
                        return null;
                    });
        }
    }

    public CompletableFuture<Peer> pingPeerAsync(InetAddress peerIp, String localUserName) {
        return pinger.pingPeer(peerIp, localUserName);
    }
}
