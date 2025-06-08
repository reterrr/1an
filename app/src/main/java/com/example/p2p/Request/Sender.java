package com.example.p2p.Request;

import com.example.p2p.InetAddressDeserializer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.net.InetAddress;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Sender {
    @JsonProperty("username")
    private String username;
    @JsonProperty("address")
    @JsonDeserialize(using = InetAddressDeserializer.class)
    private InetAddress address;
}
