package com.example.p2p;

import android.content.Context;

import com.example.p2p.Model.MyObjectBox;

import io.objectbox.BoxStore;
import io.objectbox.android.Admin;

public class ObjectBox {
    private static BoxStore boxStore;

    public static void init(Context context) {
        boxStore = MyObjectBox.builder()
                .androidContext(context.getApplicationContext())
                .build();

        new Admin(boxStore).start(context);
    }

    public synchronized static BoxStore get() {
        return boxStore;
    }

    public synchronized static void destroy() {
        boxStore.close();
    }
    public synchronized static boolean isDestroyed() {
        return boxStore.isClosed();
    }
}
