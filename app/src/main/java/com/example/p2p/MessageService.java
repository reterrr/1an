package com.example.p2p;

import com.example.p2p.Model.Chat;
import com.example.p2p.Model.User;

import java.util.Map;

public class MessageService {
    private Level level;
    private Valid valid;

    private final Map<MessageCode, String> map = Map.of();

    public interface Level {
        void onSent();

        void onReceived();

        void onWatched(User[] users);
    }

    public interface Valid {
        void onError(String message);
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public void setValid(Valid valid) {
        this.valid = valid;
    }

    public boolean validate(MessageDto dto) {
        if (dto.content.isEmpty()) {
            valid.onError(map.get(MessageCode.EMPTY_MESSAGE));

            return false;
        }

        return true;
    }


    public void send(User from, User to, MessageDto dto) {
        if (! validate(dto)) return;


    }

    private enum MessageCode {
        SUCCESS(0),
        EMPTY_MESSAGE(-15);

        public final int value;

        MessageCode(int i) {
            this.value = i;
        }
    }
}
