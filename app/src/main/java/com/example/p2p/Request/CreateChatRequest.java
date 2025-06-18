package com.example.p2p.Request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CreateChatRequest {
    @JsonProperty("sender")
    public final Sender sender;
}
