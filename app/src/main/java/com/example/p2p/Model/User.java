package com.example.p2p.Model;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

@Entity
public class User {
    @Id
    public long id;

    public String nickname;

    public ToOne<NetworkInfo> networkInfo;
}
