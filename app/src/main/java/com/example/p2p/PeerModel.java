package com.example.p2p;


public class PeerModel {

    public PeerModel(int ip, String userName) {
        this.ip = ip;
        this.userName = userName;
    }

    private final String userName;
    private final int ip;

}
