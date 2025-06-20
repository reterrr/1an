package com.example.p2p;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.p2p.Model.NetworkInfo;
import com.example.p2p.Model.User;

import java.util.ArrayList;
import java.util.List;

public class AuthPeerRepository {
    private static AuthPeerRepository instance = new AuthPeerRepository();
    private static User localUser;
    private static final MutableLiveData<List<User>> users = new MutableLiveData<>(List.of(localUser));


    private AuthPeerRepository() {
        localUser = new User(CurrentUserManager.getUser().user.getTarget());
        NetworkInfo info = localUser.networkInfo.getTarget();
        info.ip = "127.0.0.1";
        localUser.networkInfo.setTarget(info);
    }

    public static AuthPeerRepository getInstance() {
        if (instance == null)
            instance = new AuthPeerRepository();

        return instance;
    }

    public LiveData<List<User>> getUsers() {
        return users;
    }

    public synchronized void add(User user) {
        if (users.getValue().contains(user)) return;

        List<User> current = new ArrayList<>(users.getValue());
        current.add(user);
        users.postValue(current);
    }

    public synchronized void remove(User user) {
        if (!users.getValue().contains(user)) return;

        List<User> current = new ArrayList<>(users.getValue());
        current.remove(user);
        users.postValue(current);
    }

    public static void release() {
        localUser = new User(CurrentUserManager.getUser().user.getTarget());
        instance = null;
        users.postValue(List.of());
    }
}