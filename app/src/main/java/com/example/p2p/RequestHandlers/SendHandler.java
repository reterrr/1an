package com.example.p2p.RequestHandlers;


import com.example.p2p.Model.Message;
import com.example.p2p.Request.SendRequest;

import io.objectbox.Box;

public class SendHandler implements RequestHandler<SendRequest> {

    @Override
    public void handle(SendRequest request) throws Exception {
        Box<Message> messageBox;
    }

    @Override
    public Class<SendRequest> getRequestType() {
        return SendRequest.class;
    }
}
