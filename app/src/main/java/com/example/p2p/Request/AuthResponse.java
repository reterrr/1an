package com.example.p2p.Request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AuthResponse {
    @JsonProperty("sender")
    public final Sender sender;
    @JsonProperty("finger_print")
    public final String fingerPrint;
    @JsonProperty("key")
    public final String publicKeyJson;
}
