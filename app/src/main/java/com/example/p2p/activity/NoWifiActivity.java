package com.example.p2p.activity;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.p2p.databinding.ActivityNoWifiBinding;

public class NoWifiActivity extends Activity {
    private ActivityNoWifiBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNoWifiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
