package com.example.p2p.Model;

import java.util.Date;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

@Entity
public class Message {
    @Id
    public long id;
    public ToOne<Chat> chat;
    public ToOne<User> sender;

    public String content;
    public Date createdTimestamp;
    public Date updatedTimestamp;
}