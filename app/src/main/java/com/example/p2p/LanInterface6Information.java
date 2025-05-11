package com.example.p2p;

import java.net.Inet6Address;

public interface LanInterface6Information {
    Inet6Address getIp();

    Inet6Address getGatewayIp();

    short getPrefixLength();

    boolean isIpv6Enabled();
}
