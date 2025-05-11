package com.example.p2p;

import android.net.DhcpInfo;

public class WifiLanAccessor implements LanInterfaceInformation {
    public WifiLanAccessor(DhcpInfo dhcpInfo) {
        this.dhcpInfo = dhcpInfo;
    }

    @Override
    public int getIp() {
        return dhcpInfo.ipAddress;
    }

    @Override
    public int getSubnetMask() {
        return dhcpInfo.netmask;
    }

    @Override
    public int getGatewayIp() {
        return dhcpInfo.gateway;
    }

    private final DhcpInfo dhcpInfo;
}
