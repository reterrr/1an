package com.example.p2p;

public interface Visitable<T> {
    void accept(Visitor<T> t);
}
