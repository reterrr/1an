package com.example.p2p;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

final class PingServer extends UdpServer {
    private BlockingQueue<PingData> buffer;
    private ThreadPoolExecutor executor;
    private PingClient localClient;

    public PingServer(int port, int offset) {
        super(port, offset, PingServer.class.toString());

        buffer = new LinkedBlockingQueue<>();
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        executor.execute(this::process);
        localClient = new PingClient();
    }

    private void process() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                if (buffer.isEmpty()) continue;
                PingData data = buffer.take();

                switch (PingData.Message.valueOf(data.message)) {
                    case PING: {
                        try {
                            localClient.ping(
                                    new Host(data.localServerAddress, data.localServerPort),
                                    new PingData(null,
                                            NetworkResourceManager.getNetworkInfo().deviceIp,
                                            port,
                                            null
                                    )
                            );
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                        break;
                    }

                    case PONG: {


                        break;
                    }

                    default: {


                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void handle() {
        ByteBuffer buffer = ByteBuffer.wrap(data, 0, packet.getLength());
        PingData parsed = PingDataBinaryConverter.convert(buffer.array());

        this.buffer.add(parsed);
    }
}

abstract class UdpServer extends Server<DatagramSocket, DatagramPacket> {

    protected UdpServer(int port, int offset, String tag, int packetSize) {
        super(port, offset, tag, packetSize);

        this.packet = new DatagramPacket(data, data.length);
        tryOpenSocket(port);
    }

    protected UdpServer(int port, int offset, String tag) {
        this(port, offset, tag, PingInfo.maxClientPacketSize);
    }

    @Override
    protected final void receive() throws IOException {
        socket.receive(packet);
    }

    @Override
    protected final void socketClose() {
        socket.close();
    }

    protected void tryOpenSocket(int startPort) {
        for (int port = startPort; port < startPort + offset; ++port) {
            try {
                socket = new DatagramSocket(port);
                Log.i(tag, "Opened socket on port " + port);
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

public abstract class Server<SocketType, PacketType> {
    protected byte[] data;
    protected final String tag;
    protected boolean isRunning;
    protected final int port;
    protected final int offset;

    protected SocketType socket;
    protected PacketType packet;

    protected Server(int port, int offset, String tag, int packetSize) {
        this.tag = tag;
        this.port = port;
        this.offset = offset;
        this.data = new byte[packetSize];
    }

    protected Server(int port, int offset, int packetSize) {
        this(port, offset, "Server", packetSize);
    }

    public final void stop() {
        isRunning = false;
        socketClose();
    }

    protected abstract void handle();

    protected abstract void socketClose();

    protected abstract void receive() throws IOException;

    public final void start() {
        if (isRunning) return;

        isRunning = true;

        while (isRunning) {
            try {
                receive();
            } catch (IOException e) {
                Log.e(tag, "cant receive");
            }

            handle();
        }
    }
}
