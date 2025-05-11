package com.example.p2p;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UdpLanPinger implements LanInterfacePinger {
    private final String TAG;
    private DatagramSocket socket;
    private int currentPort;
    private final ExecutorService executor;
    private final char offset;

    public UdpLanPinger(String tag, char offset) {
        this.TAG = tag;
        this.offset = offset;
        this.executor = Executors.newFixedThreadPool(2);
    }

    public void tryOpenSocket(int startPort) {
        // Try opening socket on ports in range [startPort, startPort + offset)
        for (int port = startPort; port < startPort + offset; ++port) {
            try {
                socket = new DatagramSocket(port);
                currentPort = port;
                Log.i(TAG, "Opened socket on port " + port);
                return;
            } catch (SocketException e) {
                Log.w(TAG, "Port " + port + " is in use. Trying next...");
            }
        }

        // Fallback: Let the OS assign a random port
        try {
            socket = new DatagramSocket();
            currentPort = socket.getLocalPort();
            Log.i(TAG, "Opened socket on OS-assigned port " + currentPort);
        } catch (SocketException e) {
            Log.e(TAG, "Failed to open any socket", e);
        }
    }

    private void tryPing(byte[] pingData, InetAddress peerIp) {
        // Try sending ping to ports in range [currentPort, currentPort + offset)
        for (int port = currentPort; port < currentPort + offset; ++port) {
            try {
                DatagramPacket packet = new DatagramPacket(pingData, pingData.length, peerIp, port);
                socket.send(packet);
                Log.d(TAG, "Sent PING to " + peerIp.getHostAddress() + ":" + port);
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
    public CompletableFuture<Peer> pingPeer(InetAddress peerIp, String localUserName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (socket == null || socket.isClosed()) {
                    Log.e(TAG, "Socket is not initialized or closed");
                    return null;
                }

                // Send PING message
                String pingMessage = "PING:" + localUserName;
                byte[] pingData = pingMessage.getBytes();
                tryPing(pingData, peerIp);

                // Receive PONG response
                byte[] buffer = new byte[1024];
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                socket.setSoTimeout(1000); // 1-second timeout for receiving response
                socket.receive(response);

                String responseStr = new String(response.getData(), 0, response.getLength());
                if (responseStr.startsWith("PONG:")) {
                    String remoteUserName = responseStr.substring(5);
                    Log.i(TAG, "Received PONG from " + peerIp.getHostAddress() + ":" + response.getPort() + " (" + remoteUserName + ")");
                    return new Peer(peerIp, response.getPort(), remoteUserName);
                }
                Log.w(TAG, "Invalid response from " + peerIp.getHostAddress() + ": " + responseStr);
                return null;
            } catch (IOException e) {
                Log.e(TAG, "Ping failed to " + peerIp.getHostAddress() + ": " + e.getMessage());
                return null;
            }
        }, executor);
    }

    @Override
    public void close() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                Log.i(TAG, "Socket closed");
            }
            executor.shutdown();
            Log.i(TAG, "Executor shutdown initiated");
        } catch (Exception e) {
            Log.e(TAG, "Error closing pinger: " + e.getMessage());
        }
    }
}