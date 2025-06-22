package com.example.p2p.RequestHandlers;

import com.example.p2p.AuthService;
import com.example.p2p.Model.Chat;
import com.example.p2p.Model.User;
import com.example.p2p.Request.SyncChatRequest;

public class SyncHandler implements RequestHandler<SyncChatRequest> {
    @Override
    public void handle(SyncChatRequest request) throws Exception {

        AuthService.getInstance().authSender(request.sender, new AuthService.Handler() {
            @Override
            public void onError(String userName) {
                //
            }

            @Override
            public void onSuccess(User user) {
                ChatService.getInstance().sync(user, request.hash, new ChatService.SyncAction() {
                    @Override
                    public void onExisting(Chat chat) {

                    }

                    @Override
                    public void onNew(Chat chat) {

                    }
                });
            }
        });
    }

    @Override
    public Class<SyncChatRequest> getRequestType() {
        return SyncChatRequest.class;
    }
}
