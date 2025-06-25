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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.p2p.RequestHandlers.AuthRequestHandler;
import com.example.p2p.RequestHandlers.AuthResponseHandler;
import com.example.p2p.RequestHandlers.ReceiveHandler;
import com.example.p2p.RequestHandlers.SeenHandler;
import com.example.p2p.RequestHandlers.SendHandler;

import javax.net.ssl.SSLException;

public class ServerService extends Service {
    private static final String TAG = ServerService.class.toString();
    private static final String CHANNEL_ID = "SERVER_CHANNEL";

    private Server server;
    private Thread serverThread;

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
                .setContentTitle("P2P Server Running")
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
        getSystemService(NotificationManager.class).createNotificationChannel(chan);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        server = new Server(8888, boundPort -> {
            ServerInfo.setServerPort(boundPort);
            Intent ready = new Intent("com.example.p2p.ACTION_PORT_READY");
            ready.putExtra("port", boundPort);
            LocalBroadcastManager.getInstance(this).sendBroadcast(ready);
        });

        server.configMapping(r -> {
            r.register("/messages/send", new SendHandler());
            r.register("/messages/received", new ReceiveHandler());
            r.register("/messages/seen", new SeenHandler());

            r.register("/auth/request", new AuthRequestHandler());
            r.register("/auth/response", new AuthResponseHandler());

//            r.register("/chat/sync/request", );
//            r.register("/chat/sync/response", );
        });

        serverThread = new Thread(() -> {
            try {
                server.run();
            } catch (SSLException | InterruptedException e) {
                Log.e(TAG, "Server run failed", e);
            }
        }, "P2P-Server-Thread");
        serverThread.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);

        if (server != null) {
            try {
                server.shutdown();
            } catch (Exception e) {
                Log.w(TAG, "Error stopping server", e);
            }
        }

        if (serverThread != null && serverThread.isAlive()) {
            serverThread.interrupt();
            try {
                serverThread.join(1000);
            } catch (InterruptedException ignored) {
            }
        }

        super.onDestroy();
    }
}
