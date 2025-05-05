package com.example.p2p;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import com.example.p2p.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'p2p' library on application startup.
    private ActivityMainBinding binding;
    private NetworkInfoHelper wifi;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        wifi = new NetworkInfoHelper(this);

        binding.sampleText.setText(wifi.getWifiDetails());
    }
}