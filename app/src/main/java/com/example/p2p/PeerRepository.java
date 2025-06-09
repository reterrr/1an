package com.example.p2p;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class PeerRepository {
    private static final PeerRepository instance = new PeerRepository();
    private final MutableLiveData<List<Peer>> peers = new MutableLiveData<>(new ArrayList<>());

    public static PeerRepository getInstance() {
        return instance;
    }

    public LiveData<List<Peer>> getPeers() {
        return peers;
    }

    public void updatePeers(List<Peer> newList) {
        peers.postValue(newList);
    }

    public void removePeer(Peer peerToRemove) {
        List<Peer> current = new ArrayList<>(peers.getValue());
        current.remove(peerToRemove);
        peers.postValue(current);
    }

    public void addPeer(Peer peer) {
        List<Peer> current = new ArrayList<>(peers.getValue());
        current.add(peer);
        peers.postValue(current);
    }
}
