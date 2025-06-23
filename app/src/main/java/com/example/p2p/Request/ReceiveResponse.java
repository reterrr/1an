package com.example.p2p.Request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class ReceiveResponse {
    public long id;
    public Sender sender;
    public Date timeStamp;

    private ReceiveResponse(
            long id,
            Sender sender,
            Date timeStamp
    ) {
        this.id = id;
        this.sender = sender;
        this.timeStamp = timeStamp;
    }

    @JsonCreator
    public ReceiveResponse(
            @JsonProperty("id") long id,
            @JsonProperty("sender") Sender sender
    ) {
        this(id, sender, new Date());
    }
}
