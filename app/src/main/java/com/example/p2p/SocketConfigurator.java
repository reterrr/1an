package com.example.p2p;

public abstract class SocketConfigurator<SocketType> {
    public abstract void configure(SocketType socket);
}