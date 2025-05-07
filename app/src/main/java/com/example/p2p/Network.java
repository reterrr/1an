package com.example.p2p;

import androidx.annotation.NonNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Network implements Iterable<Integer> {

    public Network(NetworkInfo info) {
        this.info = info;
        //TODO: exclude device and gateway ip
        this.hosts = this.range();
    }

    @NonNull
    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < hosts.length;
            }

            @Override
            public Integer next() {
                if (!hasNext()) throw new NoSuchElementException();

                return hosts[index++];
            }
        };
    }

    private final NetworkInfo info;
    private final int[] hosts;

    private int[] range() {
        int hostMin = info.hostMin;
        int hostMax = info.hostMax;

        int[] result = new int[hostMax - hostMin + 1];

        for (int i = hostMin, j = 0; i < hostMax; ++i, ++j) {
            result[j] = i;
        }

        return result;
    }
}
