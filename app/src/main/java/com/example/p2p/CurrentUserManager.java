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
    private static CurrentUser user;

    public static CurrentUser getUser() {
        return user;
    }

    public static void setUser(CurrentUser user) {
        CurrentUserManager.user = user;
    }


    public static LoginCode login(@NonNull LoginDto loginDto) {
        Box<User> userBox = ObjectBox.get().boxFor(User.class);
        Box<CurrentUser> currentUserBox = ObjectBox.get().boxFor(CurrentUser.class);

        User user = userBox.query()
                .equal(User_.username, loginDto.username, QueryBuilder.StringOrder.CASE_SENSITIVE)
                .build()
                .findFirst();

        if (user == null) {
            return LoginCode.NO_SUCH_USER;
        }

        CurrentUser cu = currentUserBox.query()
                .equal(CurrentUser_.userId, user.id)
                .build()
                .findFirst();

        if (cu == null) {
            return LoginCode.INVALID_CREDENTIALS;
        }

        if (!BCrypt.checkpw(loginDto.password, cu.passwordHash)) {
            return LoginCode.INVALID_CREDENTIALS;
        }

        CurrentUserManager.user = cu;
        var netinfo = cu.user.getTarget().networkInfo.getTarget();

        return LoginCode.SUCCESS;
    }

}
