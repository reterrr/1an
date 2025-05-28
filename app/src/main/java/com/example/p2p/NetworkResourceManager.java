package com.example.p2p;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.wifi.WifiManager;
import android.net.DhcpInfo;

import androidx.annotation.NonNull;

public final class NetworkResourceManager {
    public static class Callback extends ConnectivityManager.NetworkCallback {
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
        }

        @Override
        public void onUnavailable() {
            super.onUnavailable();
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
        }
    }

    private static WifiManager wifiManager;
    private static ConnectivityManager connectivityManager;
    private static DhcpInfo dhcpInfo;
    private static WifiLanAccessor wifiLanAccessor;
    private static NetworkInfo networkInfo;

    public static void init(Context context) {
        if (wifiManager != null || connectivityManager != null) {
            throw new IllegalStateException("Resources already initialized");
        }

        Context appContext = context.getApplicationContext();
        wifiManager = (WifiManager) appContext.getSystemService(Context.WIFI_SERVICE);
        connectivityManager = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (wifiManager == null || connectivityManager == null) {
            throw new IllegalStateException("Failed to initialize system services");
        }

        updateNetworkResources();
    }

    private NetworkResourceManager() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static void updateNetworkResources() {
        if (wifiManager.isWifiEnabled()) {
            dhcpInfo = wifiManager.getDhcpInfo();
            wifiLanAccessor = new WifiLanAccessor(dhcpInfo);
            networkInfo = new NetworkInfo(wifiLanAccessor);
        } else {
            dhcpInfo = null;
            wifiLanAccessor = null;
            networkInfo = null;
        }
    }

    public static WifiManager getWifiManager() {
        return wifiManager;
    }

    public static DhcpInfo getDhcpInfo() {
        return dhcpInfo;
    }

    public static WifiLanAccessor getWifiLanAccessor() {
        return wifiLanAccessor;
    }

    public static NetworkInfo getNetworkInfo() {
        return networkInfo;
    }
}