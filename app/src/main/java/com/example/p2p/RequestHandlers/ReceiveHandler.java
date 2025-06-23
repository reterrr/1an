package com.example.p2p.RequestHandlers;

import com.example.p2p.AuthService;
import com.example.p2p.Model.Message;
import com.example.p2p.Model.State;
import com.example.p2p.Model.User;
import com.example.p2p.ObjectBox;
import com.example.p2p.Request.ReceiveResponse;

import io.objectbox.Box;

public class ReceiveHandler implements RequestHandler<ReceiveResponse> {
    @Override
    public void handle(ReceiveResponse receiveResponse) throws Exception {
        AuthService.getInstance().authSender(receiveResponse.sender, new AuthService.Handler() {
            @Override
            public void onError(String userName) {

            }

            @Override
            public void onSuccess(User user) {
                Box<Message> messageBox = ObjectBox.get().boxFor(Message.class);

                Message mes = messageBox.get(receiveResponse.id);
                if (mes == null) {
                    return;
                }

                mes.state = State.RECEIVED;
                messageBox.put(mes);
            }
        });
    }

    @Override
    public Class<ReceiveResponse> getRequestType() {
        return ReceiveResponse.class;
    }
}
