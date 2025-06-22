package com.example.p2p.Request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SyncChatRequest {
    @JsonProperty("hash")
    public String hash;

    @JsonProperty("sender")
    public Sender sender;

    @JsonCreator
    public SyncChatRequest(
            @JsonProperty("hash") String hash,
            @JsonProperty("sender") Sender sender
    ) {
        this.sender = sender;
        this.hash = hash;
    }
}
