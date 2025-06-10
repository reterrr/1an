package com.example.p2p;

public enum RegisterDtoCode {
    NOT_EMPTY(-2),
    PASSWORD_LENGTH(-4),
    CHARS(-5),
    CONFIRM_NOT_MATCH(-3);

    public final int value;

    RegisterDtoCode(int i) {
        this.value = i;
    }
}
