package com.example.p2p;

import android.app.Application;
import android.content.Intent;

import com.example.p2p.activity.LoginActivity;

import java.io.IOException;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

public final class MyApp extends Application {
    private static final String TAG = "MyApp";

    @Override
    public void onCreate() {
        super.onCreate();

        NetworkResourceManager.init(this);
        ObjectBox.init(this);

//        try {
//            SslContext context = SslContextBuilder
//                    .forServer(
//                            getAssets().open("cert_chain.pem"),
//                            getAssets().open("end_entity.key")
//                    )
//                    .build();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

//        NetworkInfo networkInfo = new NetworkInfo();
//        networkInfo.ip = NetworkResourceManager.getNetworkInfo().deviceIp.toString();
//        networkInfo.port = 8888;
//
//        User currentUser = new User();
//        currentUser.username = "";
//        currentUser.networkInfo.setTarget(networkInfo);

//                byte[] arr = new byte[2048];
//                certStream = getAssets().open("cert.pem");
//                for (int i = 0, j = 0; (j = certStream.read()) != -1; ++i) {
//                    arr[i] = (byte) j;
//                }
//                String certContent = new String(arr, StandardCharsets.UTF_8);
//                Log.d(TAG, "Certificate content: " + certContent);
//                certStream.close();

        // Load and log key
//                keyStream = getAssets().open("key.pem");
//                String keyContent = new String(keyStream.readAllBytes(), StandardCharsets.UTF_8);
//                Log.d(TAG, "Key content: " + keyContent);
//                keyStream.close();


    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}