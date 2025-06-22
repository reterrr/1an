package com.example.p2p.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.p2p.AuthPeerRepository;
import com.example.p2p.CurrentUserManager;
import com.example.p2p.DiscoveryService;
import com.example.p2p.Model.Chat;
import com.example.p2p.PeerRepository;
import com.example.p2p.Request.Sender;
import com.example.p2p.Request.SyncChatRequest;
import com.example.p2p.ServerService;
import com.example.p2p.activity.adapter.PeerAdapter;
import com.example.p2p.databinding.ActivityPeerListBinding;

import java.net.UnknownHostException;
import java.util.ArrayList;

public class PeerListActivity extends AppCompatActivity {
    private PeerAdapter peerAdapter;
    private ActivityPeerListBinding binding;
    private final String tag = PeerListActivity.class.toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPeerListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        PeerRepository.setObserver(this);

        binding.rvPeers.setLayoutManager(new LinearLayoutManager(this));
        peerAdapter = new PeerAdapter(new ArrayList<>(), user -> {
            Intent i = new Intent(PeerListActivity.this, ChatActivity.class);
            i.putExtra("otherUserId", user.id);

//            Chat.createChatWith(user, chat -> {
//                Intent i = new Intent(PeerListActivity.this, ChatActivity.class);
//                i.putExtra("chat_id", chat.id);
//                try {
//                    SyncChatRequest request =
//                            new SyncChatRequest(chat.sync,
//                                    Sender.fromUser(CurrentUserManager.getUser().user.getTarget())
//                            );
//                } catch (UnknownHostException e) {
//                    throw new RuntimeException(e);
//                }
            startActivity(i);
        });
        binding.rvPeers.setAdapter(peerAdapter);

        AuthPeerRepository.getInstance().getUsers().observe(this, updated ->
                peerAdapter.updateList(updated)
        );
    }
//                Chat.createChatWith(peer, chat -> {
//                    Intent i = new Intent(PeerListActivity.this, ChatActivity.class);
//                    i.putExtra("chat_id", chat.id);
//                    startActivity(i);
//                })


    @Override
    public void onBackPressed() {
        AuthPeerRepository.getInstance().release();
        PeerRepository.getInstance().release();

        stopService(new Intent(this, DiscoveryService.class));
        stopService(new Intent(this, ServerService.class));
        startActivity(new Intent(this, LoginActivity.class));
    }
}
