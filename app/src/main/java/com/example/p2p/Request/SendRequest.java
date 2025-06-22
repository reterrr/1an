package com.example.p2p.Request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendRequest {
    public long id;
    @JsonProperty("message")
    public String message;
    @JsonProperty("sender")
    public Sender sender;
    @JsonProperty("receiver")
    public Sender receiver;

    @JsonCreator
    public SendRequest(
            @JsonProperty("id") long id,
            @JsonProperty("message") String message,
            @JsonProperty("sender") Sender sender,
            @JsonProperty("receiver") Sender receiver
    ) {
        this.id = id;
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
    }
}