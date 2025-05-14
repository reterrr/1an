package com.example.p2p;

import java.net.InetAddress;

public class PingClient {
    public final UdpLanPinger pinger;

    public PingClient(int port, String tag, char offset) {
        pinger = new UdpLanPinger(tag, offset);

        pinger.tryOpenSocket(port);
    }

    public void discoverPeers(Network network) {
        for (InetAddress ip : network) {
            pinger.send(
                    new Host(ip, Host.defaultPort, Host.defaultOffset),
                    PingDataBinaryConverter.convert(
                            new PingData(PingData.Message.PING.toString(), NetworkResourceManager.getNetworkInfo().deviceIp, 4445)
                    )
            );
        }
    }
}