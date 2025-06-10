package com.example.p2p.auth;

public enum LoginCode {
    SUCCESS(0),
    NO_SUCH_USER(-15),
    FAILED(-1),
    INVALID_CREDENTIALS(-2);

    public final int value;

    LoginCode(int i) {
        this.value = i;
    }
}
