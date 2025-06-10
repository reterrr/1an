package com.example.p2p.auth;

public enum RegisterCode {
    SUCCESS(0),
    EXISTS(-15),

    FAILED(-1);

    public final int value;

    RegisterCode(int i) {
        this.value = i;
    }
}
