package com.example.p2p.RequestHandlers;

import com.example.p2p.AuthService;
import com.example.p2p.CurrentUserManager;
import com.example.p2p.Model.Message;
import com.example.p2p.Model.Message_;
import com.example.p2p.Model.State;
import com.example.p2p.Model.User;
import com.example.p2p.ObjectBox;
import com.example.p2p.Request.SeenRequest;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.query.QueryBuilder;

public class SeenHandler implements RequestHandler<SeenRequest> {
    @Override
    public void handle(SeenRequest seenRequest) throws Exception {
        AuthService.getInstance().authSender(seenRequest.sender, new AuthService.Handler() {
            @Override
            public void onError(String userName) {

            }

            @Override
            public void onSuccess(User user) {
                Box<Message> messageBox = ObjectBox.get().boxFor(Message.class);
                long cu = CurrentUserManager.getUser().user.getTargetId();

                List<Message> messages = messageBox.query()
                        .equal(Message_.senderId, cu)
                        .equal(Message_.receiverId, user.id)
//                        .lessOrEqual(Message_.createdTimestamp, seenRequest.seenTimestamp)
                        .build()
                        .find();

                messages.stream()
                        .filter(m -> m.state == State.RECEIVED)
                        .forEach(m -> m.state = State.SEEN);

                messageBox.put(messages);
            }
        });
    }

    @Override
    public Class<SeenRequest> getRequestType() {
        return SeenRequest.class;
    }
}
