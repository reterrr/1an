package com.example.p2p.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.p2p.Model.Chat;
import com.example.p2p.PeerRepository;
import com.example.p2p.activity.adapter.PeerAdapter;
import com.example.p2p.databinding.ActivityPeerListBinding;

import java.util.ArrayList;


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
            long chatId = Chat.createChatWith(peer);

            Intent i = new Intent(PeerListActivity.this, ChatActivity.class);
            i.putExtra("chat_id", chatId);
            startActivity(i);
        });

        binding.rvPeers.setAdapter(peerAdapter);

        PeerRepository.getInstance().getPeers().observe(this, updated -> {
            peerAdapter.updateList(updated);
        });
    }
}
