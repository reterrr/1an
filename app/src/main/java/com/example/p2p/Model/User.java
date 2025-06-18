package com.example.p2p.Model;

import androidx.annotation.Nullable;

import com.example.p2p.Peer;

import java.util.Date;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.annotation.Unique;
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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof User)) return false;

        User user = (User) obj;

        return username.equals(user.username) ||
                networkInfo.equals(user.networkInfo);
    }
}
