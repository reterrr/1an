package com.example.p2p.Model;

import java.util.Date;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public final class Message {
    @Id
    private long id;

    private ToOne<Chat> chat;
    private ToOne<User> sender;

    private String content;
    private Date timestamp;
}