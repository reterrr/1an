package com.example.p2p.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.example.p2p.DiscoveryService;

import androidx.appcompat.app.AppCompatActivity;

import com.example.p2p.ServerService;
import com.example.p2p.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent server = new Intent(this, ServerService.class);
        startForegroundService(server);

        Intent discovery = new Intent(this, DiscoveryService.class);
        startForegroundService(discovery);

        startActivity(new Intent(this, LoginActivity.class));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}