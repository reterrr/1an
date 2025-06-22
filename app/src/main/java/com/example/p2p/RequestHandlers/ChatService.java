package com.example.p2p.RequestHandlers;

import com.example.p2p.Model.Chat;
import com.example.p2p.Model.Chat_;
import com.example.p2p.Model.User;
import com.example.p2p.Model.User_;
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
//        Box<Chat> chatBox = ObjectBox.get().boxFor(Chat.class);
//
//        // 1) Find chat by the incoming hash (if any)
//        Chat chatByHash = chatBox.query()
//                .equal(Chat_.sync, hash, QueryBuilder.StringOrder.CASE_SENSITIVE)
//                .build()
//                .findFirst();
//
//        // 2) Find chat(s) where this user is already a participant (if any)
//        Chat chatByUser = chatBox.query()
//                .link(Chat_.participants)
//                .equal(User_.id, user.getId())
//                .build()
//                .findFirst();
//
//        if (chatByHash == null && chatByUser == null) {
//            // → No chat with this hash, and user isn't in any chat  → brand-new chat
//            action.onNewHash(hash);
//
//        } else if (chatByHash != null && chatByUser != null
//                && chatByHash.getId() == chatByUser.getId()) {
//            // → Found the same chat by hash *and* user is already in it → existing
//            action.onExisting(chatByHash);
//
//        } else if (chatByHash != null && chatByUser == null) {
//            // → Chat exists with this hash but user isn't yet a participant → add & new participant
//            chatByHash.participants.add(user);
//            chatBox.put(chatByHash);
//            action.onNew(chatByHash);
//
//        } else {
//            // → chatByHash == null && chatByUser != null
//            // Hash mismatch, and user is already in *some* other chat → error
//            action.onError(user, hash);
//        }
    }

}
