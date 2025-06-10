package com.example.p2p.auth;

public final class RegisterDto {
    public final String username;
    public final String password;
    public final String confirmPassword;

    public RegisterDto(String username, String password, String confirmPassword) {
        this.username = username;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }
}
