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

        NetworkInfo i = new NetworkInfo(new LanInterface() {

            @Override
            public int getIp() {
                return info.getIpAddress();
            }

            @Override
            public int getSubnetMask() {
                return dhcp.netmask;
            }

            @Override
            public int getGatewayIp() {
                return dhcp.gateway;
            }
        });

        return "SSID: " + info.getSSID() + "\n" +
                "BSSID: " + info.getBSSID() + "\n" +
                "IP Address: " + formatIp(info.getIpAddress()) + "\n" +
                "MAC Address: " + getMacAddress() + "\n" +
                "Gateway: " + formatIp(dhcp.gateway) + "\n" +
                "DNS 1: " + formatIp(dhcp.dns1) + "\n" +
                "DNS 2: " + formatIp(dhcp.dns2) + "\n" +
                "Netmask: " + formatIp(dhcp.netmask) + "\n";

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
