package com.example.p2p;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.p2p.Model.User;

import java.util.ArrayList;
import java.util.List;

public class AuthPeerRepository {
    private static AuthPeerRepository instance = new AuthPeerRepository();
    private static final MutableLiveData<List<User>> users = new MutableLiveData<>(new ArrayList<>());

    private AuthPeerRepository() {
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
        instance = null;
        users.postValue(new ArrayList<>());
    }
}