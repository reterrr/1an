package com.example.p2p.Request;

import android.os.Build;

import com.example.p2p.InetAddressDeserializer;

import com.example.p2p.Model.User;
import com.example.p2p.Model.UserKeys;
import com.example.p2p.Model.UserKeys_;
import com.example.p2p.Model.User_;
import com.example.p2p.NetworkResourceManager;
import com.example.p2p.ObjectBox;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.net.InetAddress;
import java.net.UnknownHostException;

import io.objectbox.Box;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;

public class Sender {
    @JsonProperty("username")
    public String username;
    @JsonProperty("address")
    @JsonDeserialize(using = InetAddressDeserializer.class)
    public InetAddress address;

    @JsonProperty("finger_print")
    public String fingerPrint;

    @JsonProperty("port")
    public long port;

    @JsonCreator
    public Sender(
            @JsonProperty("username") String username,
            @JsonProperty("address")
            @JsonDeserialize(using = InetAddressDeserializer.class)
            InetAddress address,
            @JsonProperty("port") long port,
            @JsonProperty("finger_print") String fingerPrint
    ) {
        this.address = address;
        this.port = port;
        this.username = username;
        this.fingerPrint = fingerPrint;
    }

    public static Sender fromUser(User user) throws UnknownHostException {
        Box<UserKeys> userKeysBox = ObjectBox.get().boxFor(UserKeys.class);
        QueryBuilder<UserKeys> q = userKeysBox.query();
        q.link(UserKeys_.user)
                .equal(User_.id, user.id);

        return new Sender(username2post(user.username),
                InetAddress.getByName(user.networkInfo.getTarget().ip),
                user.networkInfo.getTarget().port,
                q.build().findFirst().fingerPrint);
    }

    public static String username2post(String username) {
        return username + "@" + Build.MODEL + "@" + NetworkResourceManager.getNetworkInfo().deviceIp.getHostAddress();
    }
}
