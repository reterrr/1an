package com.example.p2p.RequestHandlers;


import android.util.Log;

import com.example.p2p.AuthService;
import com.example.p2p.Client;
import com.example.p2p.MessageService;
import com.example.p2p.Model.NetworkInfo;
import com.example.p2p.Model.User;
import com.example.p2p.Request.ReceiveResponse;
import com.example.p2p.Request.Request;
import com.example.p2p.Request.SendRequest;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SendHandler implements RequestHandler<SendRequest> {
    private static final String tag = SendHandler.class.toString();
    private final MessageService service = new MessageService();

    @Override
    public void handle(SendRequest request) throws Exception {
        AuthService.getInstance().authSender(request.sender, new AuthService.Handler() {
            @Override
            public void onError(String userName) {
                Log.e(tag, "Some unexpected traffic " + userName);
            }

            @Override
            public void onSuccess(User user) {
                try {
                    service.onReceived(request.message, user);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                NetworkInfo info = user.networkInfo.getTarget();
                ReceiveResponse response = new ReceiveResponse(request.id, request.receiver);

                try {
                    var client = Client.getInstance(InetAddress.getByName(info.ip), info.port);
                    client.send(Request.create("/message/received", response));
                } catch (UnknownHostException | JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public Class<SendRequest> getRequestType() {
        return SendRequest.class;
    }
}
