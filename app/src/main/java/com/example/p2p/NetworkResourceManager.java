package com.example.p2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.wifi.WifiManager;
import android.net.DhcpInfo;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.p2p.activity.NoWifiActivity;

public final class NetworkResourceManager {
    public static class Callback extends ConnectivityManager.NetworkCallback {
        private final Context context;

        public Callback(Context context) {
            this.context = context;
        }

        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);

            updateNetworkResources();
            //context.startActivity(new Intent(context, LoginActivity.class));
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);

            onUnavailable();
        }

        @Override
        public void onUnavailable() {
            super.onUnavailable();

            Intent intent = new Intent(context, NoWifiActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            context.stopService(new Intent(context, ServerService.class));
            context.stopService(new Intent(context, DiscoveryService.class));

            context.startActivity(intent);
        }
    }

    private static WifiManager wifiManager;
    private static ConnectivityManager connectivityManager;
    private static DhcpInfo dhcpInfo;
    private static WifiLanAccessor wifiLanAccessor;
    private static NetworkInfo networkInfo;


    public static void init(Context context) {
        if (wifiManager != null || connectivityManager != null) {
            return;
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
        dhcpInfo = wifiManager.getDhcpInfo();
        wifiLanAccessor = new WifiLanAccessor(dhcpInfo);
        networkInfo = new NetworkInfo(wifiLanAccessor);
    }

    public static com.example.p2p.Model.NetworkInfo getDeviceNetworkInfo() {
        var netInfo = new com.example.p2p.Model.NetworkInfo();

        netInfo.port = ServerInfo.getServerPort();
        netInfo.ip = getNetworkInfo().deviceIp.getHostAddress();

        return netInfo;
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