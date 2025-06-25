package com.example.p2p.Request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class SeenRequest {
    public Sender sender;
    public Date seenTimestamp;

    private SeenRequest(
            Sender sender, Date seenTimestamp
    ) {
        this.sender = sender;
        this.seenTimestamp = seenTimestamp;
    }

    @JsonCreator
    public SeenRequest(
            @JsonProperty("sender") Sender sender
    ) {
        this(sender, new Date());
    }
}
