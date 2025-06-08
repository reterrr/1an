package com.example.p2p;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.net.InetAddress;

public class InetAddressDeserializer extends JsonDeserializer<InetAddress> {
    @Override
    public InetAddress deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return InetAddress.getByName(p.getText());
    }
}