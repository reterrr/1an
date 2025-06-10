package com.example.p2p.Model;

import com.example.p2p.ChatType;
import com.example.p2p.ChatTypeConverter;
import com.example.p2p.CurrentUserManager;

import com.example.p2p.ObjectBox;
import com.example.p2p.Peer;

import java.util.Date;

import io.objectbox.Box;
import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

import io.objectbox.query.QueryBuilder;
import io.objectbox.relation.ToMany;

@Entity
public class Chat {
    @Id
    public long id;
    public String title;
    public ToMany<User> participants;
    @Convert(converter = ChatTypeConverter.class, dbType = Integer.class)
    public ChatType chatType;
    public Date createdTimestamp;

    public Chat() {
        this.createdTimestamp = new Date();
    }

    public static long createChatWith(Peer peer) {
        Box<Chat> chatBox = ObjectBox.get().boxFor(Chat.class);
        Box<User> userBox = ObjectBox.get().boxFor(User.class);
        Box<NetworkInfo> netBox = ObjectBox.get().boxFor(NetworkInfo.class);
        long currentUserId = CurrentUserManager.getUser().id;
        if (currentUserId < 0) {
            throw new IllegalStateException("No current user ID set");
        }

        String nick = peer.userName;
        String ip = peer.ip.getHostAddress();
        int port = peer.port;

        QueryBuilder<User> builder = userBox.query(
                User_.username.equal(nick)
        );

        builder.link(User_.networkInfo)
                .apply(NetworkInfo_.ip.equal(ip))
                .and()
                .apply(NetworkInfo_.port.equal(port));

        User other = builder.build().findFirst();

        if (other == null) {
            NetworkInfo ni = new NetworkInfo();
            ni.ip = ip;
            ni.port = port;
            netBox.put(ni);

            other = new User();
            other.username = nick;
            other.networkInfo.setTarget(ni);
            userBox.put(other);
        }

        long otherId = other.id;
        boolean isSelf = (otherId == currentUserId);
        ChatType chatType = isSelf ?
                ChatType.SELF :
                ChatType.CHAT;

        QueryBuilder<Chat> q = chatBox.query()
                .equal(Chat_.chatType, chatType.value);

        if (isSelf) {
            q.link(Chat_.participants)
                    .equal(User_.id, currentUserId);
        } else {
            q.link(Chat_.participants)
                    .equal(User_.id, currentUserId)
                    .and()
                    .equal(User_.id, otherId);
        }

        long existingId = q.build().findFirstId();
        if (existingId != 0) {
            return existingId;
        }

        Chat newChat = new Chat();
        newChat.title = isSelf
                ? "Saved Messages"
                : other.username;
        newChat.chatType = chatType;

        newChat.participants.add(CurrentUserManager.getUser().user.getTarget());
        if (!isSelf) {
            newChat.participants.add(other);
        }

        chatBox.put(newChat);

        return newChat.id;
    }

    public long createGroupWith(Peer[] peer) {
        return 1;
    }
}