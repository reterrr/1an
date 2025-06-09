package com.example.p2p.activity;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.p2p.Peer;

import java.util.ArrayList;
import java.util.List;

public class PeerViewModel extends AndroidViewModel {
    private final MutableLiveData<List<Peer>> peerLiveData = new MutableLiveData<>(new ArrayList<>());

    public PeerViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Peer>> getPeers() {
        return peerLiveData;
    }

    public void updatePeers(List<Peer> newPeers) {
        peerLiveData.postValue(newPeers);
    }

    public void addPeer(Peer peer) {
        List<Peer> current = peerLiveData.getValue();
        if (current == null) current = new ArrayList<>();
        if (!current.contains(peer)) {
            current = new ArrayList<>(current);
            current.add(peer);
            peerLiveData.postValue(current);
        }
    }

    public void removePeer(Peer peer) {
        List<Peer> current = peerLiveData.getValue();
        if (current == null) current = new ArrayList<>();
        if (current.contains(peer)) {
            current = new ArrayList<>(current);
            current.remove(peer);
            peerLiveData.postValue(current);
        }

    }
}
