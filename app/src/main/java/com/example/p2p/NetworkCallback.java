package com.example.p2p;

import android.net.ConnectivityManager;

@FunctionalInterface
public interface NetworkCallback {
    void configure(ConnectivityManager.NetworkCallback callback);
}
