package com.example.p2p.Model;

import androidx.annotation.Nullable;

import com.example.p2p.ObjectBox;
import com.example.p2p.Peer;
import com.example.p2p.Request.Sender;

import java.io.ObjectStreamException;
import java.util.Date;

import io.objectbox.Box;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.annotation.Unique;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;
import io.objectbox.relation.ToOne;

@Entity
public class User {
    @Id
    public long id;

    @Index
    @Unique
    public String username;

    public ToOne<NetworkInfo> networkInfo;

    public Date createdAt;

    public User() {
        this.createdAt = new Date();
    }

    public User(Peer peer) {
        this.username = peer.userName;

        NetworkInfo info = new NetworkInfo();
        info.ip = peer.ip.toString();
        info.port = peer.port;

        this.networkInfo.setTarget(info);
        this.createdAt = new Date();
    }

    public User(User user) {
        this.id = user.id;
        this.username = user.username;
        this.networkInfo.setTarget(user.networkInfo.getTarget());
        this.createdAt = user.createdAt;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof User)) return false;

        User user = (User) obj;

        return username.equals(user.username) ||
                networkInfo.equals(user.networkInfo);
    }

    public long getId() {
        return id;
    }

    public static User getUserFromSender(Sender sender) {
        Box<UserKeys> keysBox = ObjectBox.get().boxFor(UserKeys.class);
        Query<UserKeys> keyQuery = keysBox.query()
                .equal(UserKeys_.fingerPrint, sender.fingerPrint, QueryBuilder.StringOrder.CASE_SENSITIVE)
                .build();

        UserKeys found = keyQuery.findFirst();
        if (found != null) {
            return found.user.getTarget();
        }

        Box<User> userBox = ObjectBox.get().boxFor(User.class);
        Query<User> userQuery = userBox.query()
                .equal(User_.username, sender.username, QueryBuilder.StringOrder.CASE_SENSITIVE)
                .build();

        User user = userQuery.findFirst();
        if (user == null) {
            user = new User();
            user.username = sender.username;

            userBox.put(user);
        }

        UserKeys newKeys = new UserKeys();
        newKeys.user.setTarget(user);
        newKeys.fingerPrint = sender.fingerPrint;
        keysBox.put(newKeys);

        return user;
    }
}
