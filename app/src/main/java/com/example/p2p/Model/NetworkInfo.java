package com.example.p2p.Model;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class NetworkInfo {
    @Id
    public long id;

    public String ip;
    public long port;

    public NetworkInfo() {}

    public NetworkInfo(NetworkInfo info) {
        this.ip = info.ip;
        this.port = info.port;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NetworkInfo))
            return false;

        NetworkInfo info = (NetworkInfo) o;

        return ip.equals(info.ip);
    }
}