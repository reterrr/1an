package com.example.p2p;

import androidx.annotation.NonNull;

import java.net.Inet6Address;
import java.util.Iterator;

public class Network6 implements Iterable<Inet6Address> {
    public Network6(Network6Info networkInfo) {
        this.networkInfo = networkInfo;
    }

    @NonNull
    @Override
    public Iterator<Inet6Address> iterator() {
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public Inet6Address next() {
                return null;
            }
        };
    }

    private final Network6Info networkInfo;
}
