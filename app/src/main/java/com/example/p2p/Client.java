package com.example.p2p;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

final class PingClient extends UdpClient {
    public PingClient(String tag) {
        super(tag);
    }

    public PingClient() {
        this(PingClient.class.toString());
    }

    public void ping(Host host, PingData pingData) throws IOException {
        super.send(host.ip, host.port, PingDataBinaryConverter.convert(pingData));
    }

    public void discoverPeers(Network network) {
        for (InetAddress address : network) {
            try {
                ping(
                        new Host(address),
                        new PingData(
                                PingData.Message.PING.toString(),
                                NetworkResourceManager.getNetworkInfo().deviceIp,
                                PingInfo.pingServerPort
                        )
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

abstract class UdpClient extends Client<DatagramSocket> {
    protected UdpClient(String tag) {
        super(tag);
        tryOpenSocket();
    }

    @Override
    protected void send(InetAddress ip, int destinationPort, byte[] data) throws IOException {
        DatagramPacket packet = new DatagramPacket(data, data.length, ip, destinationPort);
        socket.send(packet);
    }

    protected void tryOpenSocket() {
        try {
            socket = new DatagramSocket();
            port = socket.getPort();
        } catch (SocketException e) {
            Log.e(tag, "Failed to open any socket", e);
        }
    }
}

public abstract class Client<SocketType> {
    protected int port;
    protected final String tag;
    protected SocketType socket;

    protected abstract void send(InetAddress ip, int destinationPort, byte[] data) throws IOException;

    protected Client(int port, String tag) {
        this.port = port;
        this.tag = tag;
    }

    protected Client(String tag) {
        this.tag = tag;
    }
}
