package com.example.p2p.Request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthRequest {
    @JsonProperty("sender")
    public final Sender sender;
    @JsonProperty("finger_print")
    public final String fingerPrint;
    @JsonProperty("key")
    public final String publicKeyJson;

    @JsonCreator
    public AuthRequest(
            @JsonProperty("sender") Sender sender,
            @JsonProperty("finger_print") String fingerPrint,
            @JsonProperty("key") String publicKeyJson
    ) {
        this.sender = sender;
        this.fingerPrint = fingerPrint;
        this.publicKeyJson = publicKeyJson;
    }
}
