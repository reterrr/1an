package com.example.p2p;

import com.example.p2p.Request.Request;

import java.nio.charset.StandardCharsets;
import java.util.List;

import io.netty.buffer.ByteBuf;

import io.netty.channel.ChannelHandlerContext;

import io.netty.handler.codec.ByteToMessageDecoder;

public class RequestDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() < 4) return;

        in.markReaderIndex();

        int routeLen = in.readInt();

        if (in.readableBytes() < routeLen) {
            in.resetReaderIndex(); // not enough data, reset
            return;
        }

        byte[] routeBytes = new byte[routeLen];
        in.readBytes(routeBytes);
        String route = new String(routeBytes, StandardCharsets.UTF_8);

        int payloadLen = in.readInt();

        // Wait until payload is fully available
        if (in.readableBytes() < payloadLen) {
            in.resetReaderIndex();
            return;
        }

        byte[] payload = new byte[payloadLen];
        in.readBytes(payload);

        // Construct your Request object
        Request request = new Request(route, payload);
        out.add(request);
    }
}
