package com.example.p2p;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.p2p.Model.NetworkInfo;
import com.example.p2p.Model.User;
import com.example.p2p.RequestHandlers.SendHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLException;

import io.netty.handler.ssl.util.SelfSignedCertificate;

public final class MyApp extends Application {
    private static final String TAG = "MyApp";
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void onCreate() {
        super.onCreate();


        NetworkResourceManager.init(this);
        ObjectBox.init(this);

        NetworkInfo networkInfo = new NetworkInfo();
        networkInfo.ip = NetworkResourceManager.getNetworkInfo().deviceIp.toString();
        networkInfo.port = 8888;

        User currentUser = new User();
        currentUser.username = "";
        currentUser.networkInfo.setTarget(networkInfo);



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