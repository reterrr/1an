package com.example.p2p;

import java.net.DatagramSocket;
import java.net.SocketException;

@FunctionalInterface
public interface SocketConfigurator {
    void configure(DatagramSocket socket) throws SocketException;
}