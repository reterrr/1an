package com.example.p2p.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;

import com.example.p2p.AuthPeerRepository;
import com.example.p2p.CurrentUserManager;
import com.example.p2p.DiscoveryService;

import androidx.appcompat.app.AppCompatActivity;

import com.example.p2p.NetworkResourceManager;
import com.example.p2p.ObjectBox;
import com.example.p2p.PeerRepository;
import com.example.p2p.ServerService;
import com.example.p2p.databinding.ActivityMainBinding;
import com.google.crypto.tink.hybrid.HybridConfig;

import java.security.GeneralSecurityException;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NetworkResourceManager.init(this);

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkRequest request = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();

        var networkCallback = new NetworkResourceManager.Callback(this);

        ObjectBox.init(this);
        try {
            HybridConfig.register();
        } catch (GeneralSecurityException e) {
            Log.e("tag", "Unable to register google tink");

            throw new RuntimeException(e);
        }
        cm.registerNetworkCallback(request, networkCallback);
        //startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ObjectBox.destroy();

        stopService(new Intent(this.getApplicationContext(), DiscoveryService.class));
        stopService(new Intent(this.getApplicationContext(), ServerService.class));

        CurrentUserManager.release();
        AuthPeerRepository.getInstance().release();
        PeerRepository.getInstance().release();
    }
}