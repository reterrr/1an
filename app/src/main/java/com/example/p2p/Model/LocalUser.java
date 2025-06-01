package com.example.p2p.Model;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

@Entity
public class LocalUser {
    @Id
    public long id;

    public ToOne<User> user;

    public String passwordHash;
    public String salt;  // optional, if using salted hashes
}