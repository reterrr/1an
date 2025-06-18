package com.example.p2p.Model;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

@Entity
public class UserKeys {
    @Id
    public long id;

    public ToOne<User> user;
    public String fingerPrint;

    public String publicKey;       // base64 or hex-encode
}
