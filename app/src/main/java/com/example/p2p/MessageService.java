package com.example.p2p;

import android.content.Context;
import android.util.Log;

import com.example.p2p.Model.CurrentUser;
import com.example.p2p.Model.Message;
import com.example.p2p.Model.NetworkInfo;
import com.example.p2p.Model.State;
import com.example.p2p.Model.User;
import com.example.p2p.Request.Request;
import com.example.p2p.Request.SendRequest;
import com.example.p2p.Request.Sender;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import io.objectbox.Box;

public class MessageService {
    private Valid valid;

    public static Context c;
    private final Map<MessageCode, String> map = Map.of();


    public interface Valid {
        void onError(String message);
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
        new Thread(() -> {
            if (!validate(dto)) return;

            String peerJson = E2ETool.fetchStoredPublicKey(to.id);
            NetworkInfo info = to.networkInfo.getTarget();
            Box<Message> messageBox = ObjectBox.get().boxFor(Message.class);
            Sender me;
            Sender receiver;
            String encrypted;

            try {
                me = Sender.fromUser(from);
                receiver = Sender.fromUser(to);

                encrypted = E2ETool.encryptForPeer(peerJson, dto.content);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            var message = new Message();

            message.sender.setTarget(from);
            message.content = dto.content;
            message.state = State.NOTHING;
            message.receiver.setTarget(to);

            long id = messageBox.put(message);

            SendRequest request = new SendRequest(id, encrypted, me, receiver);

            try {
                var client = Client.getInstance(InetAddress.getByName(info.ip), info.port);
                client.send(Request.create("/messages/send", request));
            } catch (UnknownHostException | JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            var afterSendMessage = messageBox.get(id);
            afterSendMessage.state = State.SENT;
            Log.d("MessageService", "Saved message ID: " + id + ", content: " + message.content);

            messageBox.put(afterSendMessage);
        }).start();
    }

    public void onReceived(String message, User from) throws Exception {
        Box<Message> messageBox = ObjectBox.get().boxFor(Message.class);

        String decrypted = E2ETool.make()
                .user(CurrentUserManager.getUser().user.getTarget())
                .context(c)
                .build()
                .decryptFromPeer(message);

        User me = CurrentUserManager.getUser().user.getTarget();

        Message newMessage = new Message();
        newMessage.sender.setTarget(from);
        newMessage.receiver.setTarget(me);
        newMessage.content = decrypted;

        newMessage.state = State.RECEIVED;

        messageBox.put(newMessage);
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
