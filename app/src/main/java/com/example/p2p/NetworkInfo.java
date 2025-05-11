package com.example.p2p;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public final class NetworkInfo {
    public final InetAddress deviceIp;
    public final InetAddress subnetMask;
    public final InetAddress gatewayIp;
    public final InetAddress networkIp;
    public final InetAddress broadcastIp;
    public final InetAddress hostMin;
    public final InetAddress hostMax;

    public NetworkInfo(LanInterfaceInformation lanInterface) {
        try {
            this.deviceIp = intToInet(reverseInt(lanInterface.getIp()));
            this.subnetMask = intToInet(reverseInt(lanInterface.getSubnetMask()));
            this.gatewayIp = intToInet(reverseInt(lanInterface.getGatewayIp()));

            int deviceInt = reverseInt(lanInterface.getIp());
            int maskInt = reverseInt(lanInterface.getSubnetMask());

            int networkInt = deviceInt & maskInt;
            int broadcastInt = networkInt | ~maskInt;
            int hostMinInt = networkInt + 1;
            int hostMaxInt = broadcastInt - 1;

            this.networkIp = intToInet(networkInt);
            this.broadcastIp = intToInet(broadcastInt);
            this.hostMin = intToInet(hostMinInt);
            this.hostMax = intToInet(hostMaxInt);
        } catch (UnknownHostException e) {
            throw new RuntimeException("Failed to convert IPs", e);
        }
    }

    private static InetAddress intToInet(int ip) throws UnknownHostException {
        byte[] bytes = ByteBuffer.allocate(4).putInt(ip).array();
        return InetAddress.getByAddress(bytes);
    }

    private static int reverseInt(int ip) {
        int reverse = 0;
        for (byte i = 24; i >= 0; i -= 8) {
            reverse |= ((ip >> 24 - i) & 0xff) << i;
        }

        return reverse;
    }
}
