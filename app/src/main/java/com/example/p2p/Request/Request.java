package com.example.p2p.Request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Request {
    private final String route;
    private byte[] payload;

    public static Request create(String route, Object request) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(request);

        return new Request(route, json.getBytes(StandardCharsets.UTF_8));
    }

    public byte[] toBytes() {
        byte[] routeBytes = route.getBytes(StandardCharsets.UTF_8);
        int totalLen = Integer.BYTES
                + routeBytes.length
                + Integer.BYTES
                + payload.length;

        ByteBuffer buffer = ByteBuffer.allocate(totalLen);
        buffer.putInt(routeBytes.length);
        buffer.put(routeBytes);
        buffer.putInt(payload.length);
        buffer.put(payload);

        return buffer.array();
    }
}