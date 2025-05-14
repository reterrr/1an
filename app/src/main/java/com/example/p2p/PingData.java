package com.example.p2p;

import androidx.annotation.NonNull;

import java.net.InetAddress;

public final class PingData {
    public enum Message {
        PING("PING");

        private final String value;

        Message(String value) {
            this.value = value;
        }

        @NonNull
        @Override
        public String toString() {
            return value;
        }
    }

    public final String message;

    public final InetAddress localServerAddress;
    public final int localServerPort;

    public PingData(String message, InetAddress serverAddress, int serverPort) {
        this.message = message;
        this.localServerAddress = serverAddress;
        this.localServerPort = serverPort;
    }
}