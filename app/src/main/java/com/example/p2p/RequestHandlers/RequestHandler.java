package com.example.p2p.RequestHandlers;

public interface RequestHandler<Request> {
    void handle(Request request) throws Exception;
    Class<Request> getRequestType();
}
