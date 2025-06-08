package com.example.p2p;

import com.example.p2p.Request.Request;
import com.example.p2p.RequestHandlers.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RouterHandler extends SimpleChannelInboundHandler<Request> {
    private final Map<String, RequestHandler<?>> handlers = new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Register a handler for a given request type.
     */
    public RouterHandler register(String type, RequestHandler<?> handler) {
        handlers.put(type, handler);

        return this;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request request) {
        String type = request.getRoute();
        RequestHandler<?> h = handlers.get(type);
        if (h == null) {
            // No handler found; optionally log or reply with an error
            return;
        }

        // Execute handler logic. It may call msg.reply(...) internally.
        try {
            Object body = mapper.readValue(request.getPayload(), h.getRequestType());

            ((RequestHandler<Object>) h).handle(body);
        } catch (Exception e) {
            e.printStackTrace();
            // Optionally: send an error response
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}