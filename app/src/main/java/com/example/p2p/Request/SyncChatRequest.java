package com.example.p2p.Request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SyncChatRequest {
    @JsonProperty("hash")
    public String hash;

    @JsonProperty("sender")
    public Sender sender;
}
