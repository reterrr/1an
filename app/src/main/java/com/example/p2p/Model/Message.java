package com.example.p2p.Model;

import com.example.p2p.ChatTypeConverter;
import com.example.p2p.MessageStateConverter;

import java.util.Date;

import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

@Entity
public class Message {
    @Id
    public long id;
    //public ToOne<Chat> chat;
    public ToOne<User> sender;
    public ToOne<User> receiver;

    public String content;
    public Date createdTimestamp;
    @Convert(converter = MessageStateConverter.class, dbType = Integer.class)
    public State state;
}