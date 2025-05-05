package com.example.p2p;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class NetworkInfoHelper {
    private final Context context;

    public NetworkInfoHelper(Context context) {
        this.context = context.getApplicationContext();
    }

    public String getWifiDetails() {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) return "WifiManager not available";

        WifiInfo info = wifiManager.getConnectionInfo();
        DhcpInfo dhcp = wifiManager.getDhcpInfo();

        StringBuilder sb = new StringBuilder();
        sb.append("SSID: ").append(info.getSSID()).append("\n");
        sb.append("BSSID: ").append(info.getBSSID()).append("\n");
        sb.append("IP Address: ").append(formatIp(info.getIpAddress())).append("\n");
        sb.append("MAC Address: ").append(getMacAddress()).append("\n");
        sb.append("Gateway: ").append(formatIp(dhcp.gateway)).append("\n");
        sb.append("DNS 1: ").append(formatIp(dhcp.dns1)).append("\n");
        sb.append("DNS 2: ").append(formatIp(dhcp.dns2)).append("\n");
        sb.append("Netmask: ").append(formatIp(dhcp.netmask)).append("\n");
        return sb.toString();
    }

    @SuppressLint("DefaultLocale")
    private String formatIp(int ip) {
        return String.format(
                "%d.%d.%d.%d",
                (ip & 0xff),
                (ip >> 8 & 0xff),
                (ip >> 16 & 0xff),
                (ip >> 24 & 0xff)
        );
    }

    private String getMacAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface iface : interfaces) {
                byte[] mac = iface.getHardwareAddress();
                if (mac == null) continue;

                StringBuilder sb = new StringBuilder();
                for (byte b : mac) {
                    sb.append(String.format("%02X:", b));
                }
                if (sb.length() > 0) sb.setLength(sb.length() - 1); // Remove last ':'
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unavailable";
    }
}
