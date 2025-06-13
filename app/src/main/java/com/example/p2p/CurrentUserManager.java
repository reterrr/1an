package com.example.p2p;

import androidx.annotation.NonNull;

import com.example.p2p.Model.CurrentUser;
import com.example.p2p.Model.CurrentUser_;
import com.example.p2p.Model.NetworkInfo;
import com.example.p2p.Model.User;
import com.example.p2p.Model.User_;
import com.example.p2p.auth.LoginCode;
import com.example.p2p.auth.LoginDto;
import com.example.p2p.auth.RegisterCode;
import com.example.p2p.auth.RegisterDto;

import org.mindrot.jbcrypt.BCrypt;

import io.objectbox.Box;
import io.objectbox.query.QueryBuilder;


public final class CurrentUserManager {
    private static CurrentUser user; // TODO: 6/13/25 probably it is better to store User 

    public static CurrentUser getUser() {
        return user;
    }

    public static void setUser(CurrentUser user) {
        CurrentUserManager.user = user;
    }


    public static LoginCode login(@NonNull LoginDto loginDto) {
        Box<User> userBox = ObjectBox.get().boxFor(User.class);
        Box<CurrentUser> currentUserBox = ObjectBox.get().boxFor(CurrentUser.class);
        Box<NetworkInfo> netInfoBox = ObjectBox.get().boxFor(NetworkInfo.class);

        User user = userBox.query()
                .equal(User_.username, loginDto.username, QueryBuilder.StringOrder.CASE_SENSITIVE)
                .build()
                .findFirst();

        if (user == null) {
            return LoginCode.NO_SUCH_USER;
        }

        QueryBuilder<CurrentUser> builder = currentUserBox.query();

        builder.link(CurrentUser_.user).equal(User_.id, user.id);

        CurrentUser cu = builder
                .build()
                .findFirst();

        if (cu == null || !BCrypt.checkpw(loginDto.password, cu.passwordHash)) {
            return LoginCode.INVALID_CREDENTIALS;
        }

        NetworkInfo deviceNet = NetworkResourceManager.getDeviceNetworkInfo();
        NetworkInfo storedNet = user.networkInfo.getTarget();

        boolean updated = false;
        if (!deviceNet.equals(storedNet)) {
            deviceNet.id = netInfoBox.put(deviceNet);
            user.networkInfo.setTarget(deviceNet);
            userBox.put(user);
            updated = true;
        }

        if (updated) {
            cu = currentUserBox.get(cu.id);
        }

        CurrentUserManager.user = cu;

        return LoginCode.SUCCESS;
    }

}
