package com.example.p2p;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.p2p.Model.NetworkInfo;
import com.example.p2p.Model.User;
import com.example.p2p.Model.User_;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

import io.objectbox.Box;

public class DiscoveryService extends Service {
    private static final String CHANNEL_ID = "DISCOVERY_CHANNEL";
    private JmDNS jmDNS;
    private Thread executionThread;
    private static final String SERVICE_TYPE = "_chat._tcp.local.";

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

        Box<User> userBox = ObjectBox.get().boxFor(User.class);
        Box<NetworkInfo> netBox = ObjectBox.get().boxFor(NetworkInfo.class);

        List<User> users = userBox.query(
                        User_.nickname.equal(nickname)
                )
                .build()
                .find();

        for (User user : users) {
            NetworkInfo netInfo = user.networkInfo.getTarget();
            if (netInfo != null && ip.equals(netInfo.ip) && port == netInfo.port) {
                netBox.remove(netInfo.id);
                userBox.remove(user.id);
                Log.d(DiscoveryService.class.getSimpleName(), "Removed peer from DB: " + nickname);
                return;
            }
        }

        Log.d(DiscoveryService.class.getSimpleName(), "Peer not found in DB: " + nickname);
    }


    private void addPeer(ServiceInfo info) {
        if (info.getInetAddresses().length == 0) {
            Log.w(DiscoveryService.class.getSimpleName(), "No IP addresses found for service: " + info.getName());

            return;
        }

        String nickname = info.getName();
        String ip = info.getInetAddresses()[0].getHostAddress();
        long port = info.getPort();

        Box<User> userBox = ObjectBox.get().boxFor(User.class);
        Box<NetworkInfo> netInfoBox = ObjectBox.get().boxFor(NetworkInfo.class);

        List<User> existingUsers = userBox.query(
                        User_.nickname.equal(nickname)
                )
                .build()
                .find();

        for (User existing : existingUsers) {
            NetworkInfo ni = existing.networkInfo.getTarget();
            if (ni != null && ni.ip.equals(ip) && ni.port == port) {
                Log.d(DiscoveryService.class.getSimpleName(), "Peer already exists: " + nickname);
                return;
            }
        }

        NetworkInfo networkInfo = new NetworkInfo();
        networkInfo.ip = ip;
        networkInfo.port = port;
        netInfoBox.put(networkInfo);

        User user = new User();
        user.nickname = nickname;
        user.networkInfo.setTarget(networkInfo);
        userBox.put(user);

        Log.d(DiscoveryService.class.getSimpleName(), "Added peer: " + nickname);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        executionThread = new Thread(() -> {
            try {
                InetAddress deviceIp = NetworkResourceManager.getNetworkInfo().deviceIp;
                if (deviceIp == null) {
                    Log.e(DiscoveryService.class.getSimpleName(), "Device IP is null");
                    stopSelf();
                    return;
                }

                ServiceInfo serviceInfo = ServiceInfo.create(
                        SERVICE_TYPE,
                        "username_@" + Build.MODEL,
                        ServerInfo.getServerPort(),
                        "P2P Chat Service"
                );

                WifiManager wifi = NetworkResourceManager.getWifiManager();
                WifiManager.MulticastLock lock = wifi.createMulticastLock("JmDNS");
                lock.setReferenceCounted(true);
                lock.acquire();

                jmDNS = JmDNS.create(deviceIp, Build.MODEL);
                jmDNS.registerService(serviceInfo);
                jmDNS.addServiceListener(SERVICE_TYPE, listener);

                lock.release();
            } catch (IOException e) {
                Log.e(DiscoveryService.class.getSimpleName(), "Failed to initialize JmDNS", e);
                stopSelf();

            }
        });

        executionThread.start();

        return START_STICKY;
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
    }

    private void createNotificationChannel() {
        NotificationChannel chan = new NotificationChannel(
                CHANNEL_ID,
                "Discovery Service",
                NotificationManager.IMPORTANCE_LOW
        );

        getSystemService(NotificationManager.class)
                .createNotificationChannel(chan);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            if (jmDNS != null) {
                jmDNS.removeServiceListener(SERVICE_TYPE, listener);
                jmDNS.unregisterAllServices();
                jmDNS.close();
                jmDNS = null;
            }
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "Failed to close JmDNS", e);
        } finally {
            DiscoveredPeers.peers.clear();
            if (executionThread != null) {
                executionThread.interrupt();
                executionThread = null;
            }

            WifiManager wifi = NetworkResourceManager.getWifiManager();
            WifiManager.MulticastLock lock = wifi.createMulticastLock("JmDNS");
            if (lock.isHeld()) {
                lock.release();
            }
        }
    }
}