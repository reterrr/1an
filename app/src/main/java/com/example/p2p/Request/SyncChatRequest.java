package com.example.p2p.Request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SyncChatRequest {
    @JsonProperty("hash")
    public String hash;

    @JsonProperty("sender")
    public Sender sender;

    @JsonProperty("finger_print")
    public String fingerPrint;

    @JsonCreator
    public SyncChatRequest(
            @JsonProperty("hash") String hash,
            @JsonProperty("sender") Sender sender,
            @JsonProperty("finger_print") String fingerPrint
    ) {
        this.sender = sender;
        this.hash = hash;
        this.fingerPrint = fingerPrint;
    }
}
