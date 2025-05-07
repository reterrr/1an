package com.example.p2p;

import java.util.ArrayList;
import java.util.List;

//TODO: make it more abstract i guess
public class PingService {
    public List<Integer> getAlivePeers(Network network) {
        List<Integer> result = new ArrayList<>();

        for (int peer : network) {
            boolean i = pingPeer(peer);

            if (!i) continue;

            result.add(peer);
        }

        return result;
    }

    public boolean pingPeer(int peer) {
        //TODO: ping logic with udp etc.
        return false;
    }
}
