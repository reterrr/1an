package com.example.p2p.RequestHandlers;


import com.example.p2p.Request.SendRequest;

public class SendHandler implements RequestHandler<SendRequest> {

    @Override
    public void handle(SendRequest request) throws Exception {

    }

    @Override
    public Class<SendRequest> getRequestType() {
        return SendRequest.class;
    }
}
