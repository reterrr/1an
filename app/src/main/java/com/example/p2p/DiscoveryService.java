package com.example.p2p;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;


public class DiscoveryService extends Service {
    private static final String CHANNEL_ID = "DISCOVERY_CHANNEL";
    private static final String SERVICE_TYPE = "_chat._tcp.local.";
    private static final String TAG = DiscoveryService.class.toString();
    private JmDNS jmDNS;
    private WifiManager.MulticastLock lock;

    private final ServiceListener listener = new ServiceListener() {
        @Override
        public void serviceAdded(ServiceEvent event) {
            jmDNS.requestServiceInfo(event.getType(), event.getName(), 1000);
        }

        @Override
        public void serviceRemoved(ServiceEvent event) {
            removePeer(event.getInfo());
        }

        @Override
        public void serviceResolved(ServiceEvent event) {
            addPeer(event.getInfo());
        }
    };

    private void removePeer(ServiceInfo info) {
        String nickname = info.getName();
        String ip = info.getInetAddresses().length > 0 ? info.getInetAddresses()[0].getHostAddress() : null;
        long port = info.getPort();

        if (ip == null) {
            Log.w(DiscoveryService.class.getSimpleName(), "Cannot remove peer: No IP address found.");
            return;
        }

        try {
            Peer peer = new Peer(nickname, InetAddress.getByName(ip), (int) port);
            if (PeerRepository.getInstance().getPeers().getValue().contains(peer))
                PeerRepository.getInstance().getPeers().getValue().remove(peer);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }


    private void addPeer(ServiceInfo info) {
        if (info.getInetAddresses().length == 0) {
            Log.w(DiscoveryService.class.getSimpleName(), "No IP addresses found for service: " + info.getName());

            return;
        }

        String nickname = info.getName();
        String ip = info.getInetAddresses()[0].getHostAddress();
        long port = info.getPort();

        try {
            Peer peer = new Peer(nickname, InetAddress.getByName(ip), (int) port);
            if (!PeerRepository.getInstance().getPeers().getValue().contains(peer))
                PeerRepository.getInstance().addPeer(peer);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        Log.d(DiscoveryService.class.getSimpleName(), "Added peer: " + nickname);
    }

    private final BroadcastReceiver portReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            int port = intent.getIntExtra("port", 0);
            if (port == 0) return;

            LocalBroadcastManager.getInstance(DiscoveryService.this)
                    .unregisterReceiver(this);

            registerMdns(port);
        }
    };

    private void createNotificationChannel() {
        NotificationChannel chan = new NotificationChannel(
                CHANNEL_ID,
                "Discovery Service",
                NotificationManager.IMPORTANCE_LOW
        );

        getSystemService(NotificationManager.class)
                .createNotificationChannel(chan);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

        Notification notif = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("P2P Discovery Running")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        startForeground(101, notif);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(portReceiver, new IntentFilter("com.example.p2p.ACTION_PORT_READY"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void registerMdns(int port) {
        try {
            InetAddress deviceIp = NetworkResourceManager.getNetworkInfo().deviceIp;
            if (deviceIp == null) {
                Log.e(TAG, "Device IP is null");
                stopSelf();
                return;
            }
            WifiManager wifi = NetworkResourceManager.getWifiManager();
            lock = wifi.createMulticastLock("JmDNS");
            lock.setReferenceCounted(true);
            lock.acquire();
            jmDNS = JmDNS.create(deviceIp, Build.MODEL);

            ServiceInfo serviceInfo = ServiceInfo.create(
                    SERVICE_TYPE,
                    "username_@" + Build.MODEL,
                    port,
                    "P2P Chat Service"
            );
            jmDNS.registerService(serviceInfo);
            jmDNS.addServiceListener(SERVICE_TYPE, listener);

            Log.i(TAG, "mDNS service registered on port " + port);
        } catch (IOException e) {
            Log.e(TAG, "Failed to register mDNS", e);
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (jmDNS != null) {
            jmDNS.removeServiceListener(SERVICE_TYPE, listener);
            jmDNS.unregisterAllServices();
            try {
                jmDNS.close();
            } catch (IOException ignored) {
            }
        }
        if (lock != null && lock.isHeld()) {
            lock.release();
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(portReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

