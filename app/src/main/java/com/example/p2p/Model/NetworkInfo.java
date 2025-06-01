package com.example.p2p.Model;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class NetworkInfo {
    @Id
    public long id;

    public String ip;
    public long port;
}