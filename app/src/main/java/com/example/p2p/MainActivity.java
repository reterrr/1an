package com.example.p2p;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.p2p.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable peerUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            updateUiWithPeers();
            handler.postDelayed(this, 3000);
        }
    };

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = new Intent(this, DiscoveryService.class);
        startService(intent);

        handler.post(peerUpdateRunnable);
    }

    private void updateUiWithPeers() {
        StringBuilder sb = new StringBuilder();
        if (DiscoveredPeers.peers.isEmpty()) {
            sb.append("No peers discovered.");
        } else {
            for (Peer peer : DiscoveredPeers.peers) {
                sb.append(peer.userName)
                        .append(" @ ")
                        .append(peer.ip != null ? peer.ip.getHostAddress() : "Unknown IP")
                        .append(":")
                        .append(peer.port)
                        .append("\n");
            }
        }

        binding.sampleText.setText(sb.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Intent intent = new Intent(this, DiscoveryService.class);
        stopService(intent);
        handler.removeCallbacks(peerUpdateRunnable);
    }
}