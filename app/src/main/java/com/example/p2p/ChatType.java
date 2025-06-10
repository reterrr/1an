package com.example.p2p;

public enum ChatType {
    CHAT(0),
    GROUP(1),
    SELF(2);

    public final int value;

    ChatType(int i) {
        this.value = i;
    }
}
