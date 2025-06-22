package com.example.p2p;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.p2p.Model.User;

import java.util.ArrayList;
import java.util.List;

public class AuthPeerRepository {
    private static AuthPeerRepository instance;
    private static User localUser;
    private static MutableLiveData<List<User>> users;

    private AuthPeerRepository() {
        users = new MutableLiveData<>(new ArrayList<>());
//        localUser = new User(CurrentUserManager.getUser().user.getTarget());
//        NetworkInfo info = localUser.networkInfo.getTarget();
//        info.ip = "127.0.0.1";
//        info.port = info.port + 1;
//        localUser.networkInfo.setTarget(info);
//        add();
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

    public void release() {
        localUser = null;
        instance = null;
        users.postValue(List.of());
    }
}