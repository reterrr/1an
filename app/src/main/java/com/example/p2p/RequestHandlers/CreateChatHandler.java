package com.example.p2p.RequestHandlers;

import com.example.p2p.Request.CreateChatRequest;

public class CreateChatHandler implements RequestHandler<CreateChatRequest> {
    @Override
    public void handle(CreateChatRequest request) throws Exception {

    }

    @Override
    public Class<CreateChatRequest> getRequestType() {
        return CreateChatRequest.class;
    }
}
