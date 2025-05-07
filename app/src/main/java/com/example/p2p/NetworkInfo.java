package com.example.p2p;

public final class NetworkInfo {
    public NetworkInfo(LanInterface lanInterface) {
        this.deviceIp = lanInterface.getIp();
        this.subnetMask = lanInterface.getSubnetMask();
        this.gatewayIp = lanInterface.getGatewayIp();

        this.networkIp = this.getNetworkIp();
        this.broadcastIp = this.getBroadcastIp();
        this.hostMin = this.getHostMin();
        this.hostMax = this.getHostMax();
    }

    //TODO: do NetworkInfo class needs deviceIp and gatewayIp??
    public final int deviceIp;
    public final int broadcastIp;
    public final int subnetMask;
    public final int hostMin;
    public final int hostMax;
    public final int networkIp;
    public final int gatewayIp;

    private int getNetworkIp() {
        return deviceIp & subnetMask;
    }

    private int getBroadcastIp() {
        return networkIp | ~subnetMask;
    }

    private int getHostMin() {
        return networkIp + 1;
    }

    private int getHostMax() {
        return broadcastIp - 1;
    }
}
