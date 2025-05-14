package com.example.p2p;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UdpLanPinger implements LanInterfacePinger {
    private final String TAG;
    private DatagramSocket socket;
    private final int offset;

    public UdpLanPinger(String tag, int offset) {
        this.TAG = tag;
        this.offset = offset;
    }

    public void tryOpenSocket(int startPort) {
        for (int port = startPort; port < startPort + offset; ++port) {
            try {
                socket = new DatagramSocket(port);
                Log.i(TAG, "Opened socket on port " + port);
                return;
            } catch (SocketException e) {
                Log.w(TAG, "Port " + port + " is in use. Trying next...");
            }
        }

        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            Log.e(TAG, "Failed to open any socket", e);
        }
    }

    private void tryPing(Host host, byte[] pingData) {
        for (int port = host.port; port < host.port + host.portOffset; ++port) {
            try {
                DatagramPacket packet = new DatagramPacket(pingData, pingData.length, host.ip, port);
                socket.send(packet);
                Log.d(TAG, "Sent PING to " + host.ip.getHostAddress() + ":" + port);
            } catch (IOException e) {
                Log.w(TAG, "Failed to ping port " + port + ": " + e.getMessage());
            }
        }
    }

    public void configureSocket(SocketConfigurator configurator) throws SocketException {
        if (socket != null) {
            configurator.configure(socket);
            Log.i(TAG, "Socket configured");
        } else {
            Log.w(TAG, "Socket is null, cannot configure");
        }
    }

    @Override
    public void send(Host expectedHost, byte[] data) {
        if (socket == null || socket.isClosed()) {
            Log.e(TAG, "Socket is not initialized or closed");
            return;
        }
        tryPing(expectedHost, data);
    }

    @Override
    public void close() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                Log.i(TAG, "Socket closed");
            }
            Log.i(TAG, "Executor shutdown initiated");
        } catch (Exception e) {
            Log.e(TAG, "Error closing pinger: " + e.getMessage());
        }
    }
}