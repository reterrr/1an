package com.example.p2p.auth;

public final class LoginDto {
    public final String username;
    public final String password;

    public LoginDto(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
