package com.example.p2p.RequestHandlers;

import com.example.p2p.Model.Chat;
import com.example.p2p.Model.Chat_;
import com.example.p2p.Model.User;
import com.example.p2p.ObjectBox;

import io.objectbox.Box;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;

public class ChatService {
    private static ChatService instance;

    private ChatService() {
    }

    public static ChatService getInstance() {
        if (instance == null)
            instance = new ChatService();

        return instance;
    }

    public interface SyncAction {
        void onExisting(Chat chat);

        void onNew(Chat chat);
    }

    public void sync(User user, String hash, SyncAction action) {
        Box<Chat> chatBox = ObjectBox.get().boxFor(Chat.class);

        Query<Chat> qb = chatBox.query()
                .equal(Chat_.sync, hash, QueryBuilder.StringOrder.CASE_SENSITIVE)
                .build();
        Chat chat = qb.findFirst();

        if (chat == null) {
            Chat newChat = new Chat();
            newChat.sync = hash;
            newChat.participants.add(user);
            chatBox.put(newChat);
            action.onNew(newChat);
        } else {
            boolean isParticipant = chat.participants
                    .stream()
                    .map(User::getId)
                    .anyMatch(id -> id == user.getId());
            if (!isParticipant) {
                chat.participants.add(user);
                chatBox.put(chat);
            }

            action.onExisting(chat);
        }
    }
}
