package com.example.p2p.Model;

public enum State {
    NOTHING(0),
    SENT(1),
    RECEIVED(2),
    SEEN(3);

    public final int value;

    State(int value) {
        this.value = value;
    }
}
