package com.example.p2p;

import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;

public class WifiLanAccessor implements LanInterface {
    private final WifiInfo wifiInfo;
    private final DhcpInfo dhcpInfo;

    public WifiLanAccessor(WifiInfo wifiInfo, DhcpInfo dhcpInfo) {
        this.wifiInfo = wifiInfo;
        this.dhcpInfo = dhcpInfo;
    }

    @Override
    public int getIp() {
        return wifiInfo.getIpAddress();
    }

    @Override
    public int getSubnetMask() {
        return dhcpInfo.netmask;
    }

    @Override
    public int getGatewayIp() {
        return dhcpInfo.gateway;
    }
}
