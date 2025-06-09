package com.example.p2p.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.p2p.Peer;
import com.example.p2p.PeerRepository;
import com.example.p2p.databinding.ActivityPeerListBinding;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class PeerListActivity extends AppCompatActivity {

    private PeerAdapter peerAdapter;
    private ActivityPeerListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPeerListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.rvPeers.setLayoutManager(new LinearLayoutManager(this));
        peerAdapter = new PeerAdapter(new ArrayList<>(), peer -> {

        });

        binding.rvPeers.setAdapter(peerAdapter);

        PeerRepository.getInstance().getPeers().observe(this, updated -> {
            peerAdapter.updateList(updated);
        });
    }
}
