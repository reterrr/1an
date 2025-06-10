package com.example.p2p.Model;

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
}
