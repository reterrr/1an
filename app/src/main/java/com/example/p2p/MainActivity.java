package com.example.p2p;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.example.p2p.Model.NetworkInfo;
import com.example.p2p.Model.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.p2p.databinding.ActivityMainBinding;

import java.util.List;

import io.objectbox.Box;

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

        Intent discovery = new Intent(this, DiscoveryService.class);
        startForegroundService(discovery);

        Intent server = new Intent(this, ServerService.class);
        startForegroundService(server);

        handler.post(peerUpdateRunnable);
    }

    private void updateUiWithPeers() {
        StringBuilder sb = new StringBuilder();

        Box<User> userBox = ObjectBox.get().boxFor(User.class);

        List<User> peers = userBox.query()
                .build()
                .find();

        if (peers.isEmpty()) {
            sb.append("No peers discovered.");
        } else {
            for (User peer : peers) {
                NetworkInfo net = peer.networkInfo.getTarget();
                String ip = net != null ? net.ip : "Unknown IP";
                long port = net != null ? net.port : -1;

                sb.append(peer.nickname)
                        .append(" @ ")
                        .append(ip)
                        .append(":")
                        .append(port)
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