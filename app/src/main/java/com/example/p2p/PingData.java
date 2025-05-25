package com.example.p2p;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.net.InetAddress;

public final class PingData {
    public enum Message {
        PING("PING"),
        PONG("PONG");

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
    public final JSONObject json;

    public final InetAddress localServerAddress;
    public final int localServerPort;

    public PingData(String message, InetAddress serverAddress, int serverPort, JSONObject json) {
        this.message = message;
        this.localServerAddress = serverAddress;
        this.localServerPort = serverPort;

        this.json = json;
    }
}