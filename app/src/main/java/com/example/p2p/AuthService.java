package com.example.p2p;

import com.example.p2p.Model.NetworkInfo;
import com.example.p2p.Model.User;
import com.example.p2p.Model.UserKeys;
import com.example.p2p.Model.UserKeys_;
import com.example.p2p.Model.User_;
import com.example.p2p.Request.AuthRequest;

import io.objectbox.Box;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;

public class AuthService {
    private static final AuthService instance = new AuthService();

    public interface Handler {
        void onError(String userName);

        void onSuccess(User user);
    }

    public static AuthService getInstance() {
        return instance;
    }

    public void handleReq(AuthRequest request, Handler h) {
        Box<UserKeys> userKeysBox = ObjectBox.get().boxFor(UserKeys.class);
        Box<User> userBox = ObjectBox.get().boxFor(User.class);
        Box<NetworkInfo> networkInfoBox = ObjectBox.get().boxFor(NetworkInfo.class);

        String publicKey = request.publicKeyJson;
        String fingerPrint = request.fingerPrint;
        String userName = request.sender.username;

        QueryBuilder<UserKeys> builder = userKeysBox.query();
        builder.link(UserKeys_.user)
                .equal(User_.username, userName, QueryBuilder.StringOrder.CASE_SENSITIVE);

        Query<UserKeys> existing = builder.build();

        if (existing.find().isEmpty()) {
            NetworkInfo info = new NetworkInfo();
            UserKeys userKeys = new UserKeys();
            User user = new User();

            info.ip = request.sender.address.toString();
            info.port = request.sender.port;

            long infoId = networkInfoBox.put(info);

            user.networkInfo.setTargetId(infoId);
            user.username = userName;

            long userId = userBox.put(user);
            user = userBox.get(userId);

            userKeys.fingerPrint = fingerPrint;
            userKeys.publicKey = publicKey;
            userKeys.user.setTargetId(userId);

            userKeysBox.put(userKeys);

            h.onSuccess(user);

            return;
        }

        QueryBuilder<UserKeys> matchFp = userKeysBox.query()
                .equal(UserKeys_.fingerPrint, fingerPrint, QueryBuilder.StringOrder.CASE_SENSITIVE);

        matchFp.link(UserKeys_.user)
                .equal(User_.username, userName, QueryBuilder.StringOrder.CASE_SENSITIVE);

        UserKeys found = matchFp.build().findFirst();
        if (found != null) {
            h.onSuccess(found.user.getTarget());
        } else {
            h.onError(userName);
        }

    }

    public void handleRes() {
    }
}
