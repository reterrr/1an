package com.example.p2p.Model;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

@Entity
public class CurrentUser {
    @Id
    public long id;

    public ToOne<User> user;
    public String passwordHash;
}
