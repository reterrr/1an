package com.example.p2p.RequestHandlers;

import com.example.p2p.Request.SyncChatRequest;

public class SyncHandler implements RequestHandler<SyncChatRequest> {
    @Override
    public void handle(SyncChatRequest request) throws Exception {

    }

    @Override
    public Class<SyncChatRequest> getRequestType() {
        return SyncChatRequest.class;
    }
}
