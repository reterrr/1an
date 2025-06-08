package com.example.p2p.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Request {
    private final String route;
    private byte[] payload;
}