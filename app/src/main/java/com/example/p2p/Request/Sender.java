package com.example.p2p.Request;

import android.os.Build;

import com.example.p2p.InetAddressDeserializer;

import com.example.p2p.Model.User;
import com.example.p2p.NetworkResourceManager;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.net.InetAddress;
import java.net.UnknownHostException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Sender {
    @JsonProperty("username")
    public String username;
    @JsonProperty("address")
    @JsonDeserialize(using = InetAddressDeserializer.class)
    public InetAddress address;

    @JsonProperty("port")
    public long port;

    @JsonCreator
    public Sender(
            @JsonProperty("username") String username,
            @JsonProperty("address")
            @JsonDeserialize(using = InetAddressDeserializer.class)
            InetAddress address,
            @JsonProperty("port") long port
    ) {
        this.address = address;
        this.port = port;
        this.username = username;
    }

    public static Sender fromUser(User user) throws UnknownHostException {
        return new Sender(username2post(user.username), InetAddress.getByName(user.networkInfo.getTarget().ip), user.networkInfo.getTarget().port);
    }

    public static String username2post(String username) {
        return username + "@" + Build.MODEL + "@" + NetworkResourceManager.getNetworkInfo().deviceIp.getHostAddress();
    }
}
