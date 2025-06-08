package com.example.p2p.Request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SendRequest {
    @JsonProperty("full_name")
    private String message;
    @JsonProperty("sender")
    private Sender sender;
}