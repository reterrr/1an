package com.example.p2p;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

public final class PingServer {
    private ArrayList<PingData> buffer;

    private final String tag;
    private DatagramSocket socket;
    private boolean isRunning;
    private final int offset;

    public PingServer(int port, int offset, String tag) {
        this.tag = tag;
        this.offset = offset;

        tryOpenSocket(port);
    }

    public void start() throws IOException {
        isRunning = true;

        while (isRunning) {
            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
            socket.receive(packet);

            buffer.add(PingDataBinaryConverter.convert(packet.getData()));
        }
    }

    public void tryOpenSocket(int startPort) {
        for (int port = startPort; port < startPort + offset; ++port) {
            try {
                socket = new DatagramSocket(port);
                Log.i(tag, "Opened socket on port " + port);
                return;
            } catch (SocketException e) {
                Log.w(tag, "Port " + port + " is in use. Trying next...");
            }
        }

        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            Log.e(tag, "Failed to open any socket", e);
        }
    }
}
