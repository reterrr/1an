package com.example.p2p;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.p2p.Model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PeerRepository {
    private static final List<Peer> previousPeers = new ArrayList<>();
    private static final MutableLiveData<List<Peer>> peers = new MutableLiveData<>(new ArrayList<>());

    private static PeerRepository instance;

    private static Observer<List<Peer>> peerObserver;

    private PeerRepository() {
        // <-- Observer moved here, not in a static block
    }

    public static synchronized PeerRepository getInstance() {
        if (instance == null) {
            instance = new PeerRepository();
        }

        return instance;
    }

    public LiveData<List<Peer>> getPeers() {
        return peers;
    }

    /**
     * Removes a peer from the list if present.
     */
    public synchronized void removePeer(Peer peerToRemove) {
        List<Peer> current = new ArrayList<>(peers.getValue());
        if (current.remove(peerToRemove)) {
            peers.postValue(current);
        }
    }

    /**
     * Adds a peer to the list if not already present.
     */
    public synchronized void addPeer(Peer peer) {
        List<Peer> current = new ArrayList<>(peers.getValue());
        if (!current.contains(peer)) {
            current.add(peer);
            peers.postValue(current);
        }
    }

    public static void setObserver(LifecycleOwner owner) {
        if (peerObserver != null) {
            peers.removeObserver(peerObserver);
        }

        peerObserver = currentPeers -> {
            List<Peer> added = new ArrayList<>(currentPeers);
            added.removeAll(previousPeers);

            List<Peer> removed = new ArrayList<>(previousPeers);
            removed.removeAll(currentPeers);
            List<User> removedUsers = removed.stream()
                    .map(User::new)
                    .collect(Collectors.toList());

            added.forEach(peer -> peer.accept(new AuthVisitor()));

            previousPeers.clear();
            previousPeers.addAll(currentPeers);

            removedUsers.forEach(AuthPeerRepository.getInstance()::remove);
        };

        peers.observe(owner, peerObserver);
    }

    public static void release() {
        peers.removeObserver(peerObserver);

        previousPeers.clear();
        peers.postValue(new ArrayList<>());
        instance = null;
    }
}
