package com.example.p2p;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.p2p.RequestHandlers.SendHandler;

import javax.net.ssl.SSLException;

public class ServerService extends Service {
    private static final String TAG = ServerService.class.toString();
    private static final String CHANNEL_ID = "SERVER_CHANNEL";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

        Notification notif = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("P2P Discovery Running")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        startForeground(102, notif);
    }

    private void createNotificationChannel() {
        NotificationChannel chan = new NotificationChannel(
                CHANNEL_ID,
                "Server Service",
                NotificationManager.IMPORTANCE_LOW
        );

        getSystemService(NotificationManager.class)
                .createNotificationChannel(chan);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Server server = new Server(8888);
        server.configMapping(r -> r.register("/messages/send", new SendHandler()));

        new Thread(() -> {
            try {
                server.run();
                ServerInfo.setServerPort(server.getPort());
                Log.i(TAG, "Server started successfully on port " + server.getPort());
            } catch (SSLException |
                     InterruptedException e) {
                Log.e(TAG, "Failed to start server", e);
                throw new RuntimeException("Failed to start server", e);
            }
        });

        return START_STICKY;
    }
}
