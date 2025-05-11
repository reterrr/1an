package com.example.p2p;

import java.net.Inet6Address;

//TODO: implement ipv6 address
public class WifiLan6Information implements LanInterface6Information {

    @Override
    public Inet6Address getIp() {
        return null;
    }

    @Override
    public Inet6Address getGatewayIp() {
        return null;
    }

    @Override
    public short getPrefixLength() {
        return 0;
    }

    @Override
    public boolean isIpv6Enabled() {
        return false;
    }
}
