package com.example.p2p;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Build;
import android.util.Log;

import com.example.p2p.databinding.ActivityMainBinding;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private JmDNS jmDNS;
    private final Map<String, Peer> discoveredPeers = new HashMap<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String deviceName = Build.MODEL + "-" + UUID.randomUUID();

        new Thread(() -> {
            try {
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiManager.MulticastLock multicastLock = wifiManager.createMulticastLock("mdnsLock");
                multicastLock.setReferenceCounted(true);
                multicastLock.acquire();

                InetAddress localIp = getLocalIpAddress(getApplicationContext());

                jmDNS = JmDNS.create(localIp, deviceName);

                ServiceInfo serviceInfo = ServiceInfo.create(
                        "_chat._tcp.local.",
                        "user@" + deviceName,
                        4444,
                        "Lan messenger"
                );
                jmDNS.registerService(serviceInfo);

                jmDNS.addServiceListener("_chat._tcp.local.", new ServiceListener() {
                    @Override
                    public void serviceAdded(ServiceEvent event) {
                        jmDNS.requestServiceInfo(event.getType(), event.getName(), true);
                    }

                    @Override
                    public void serviceRemoved(ServiceEvent event) {
                        runOnUiThread(() -> {
                            discoveredPeers.remove(event.getName());
                            updateUiWithPeers();
                        });
                    }

                    @Override
                    public void serviceResolved(ServiceEvent event) {
                        ServiceInfo info = event.getInfo();
                        if (info.getInetAddresses().length == 0) return;
                        Peer peer = new Peer(
                                info.getName(),
                                info.getInetAddresses()[0],
                                info.getPort()
                        );

                        runOnUiThread(() -> {
                            discoveredPeers.put(info.getName(), peer);
                            updateUiWithPeers();
                        });
                    }
                });

            } catch (IOException e) {
                Log.e("JMDNS", "Failed to initialize", e);
            }
        }).start();
    }

    private InetAddress getLocalIpAddress(Context context) throws IOException {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int ip = wifiManager.getConnectionInfo().getIpAddress();
        byte[] bytes = {
                (byte) (ip & 0xff),
                (byte) (ip >> 8 & 0xff),
                (byte) (ip >> 16 & 0xff),
                (byte) (ip >> 24 & 0xff)
        };
        return InetAddress.getByAddress(bytes);
    }

    private void updateUiWithPeers() {
        StringBuilder sb = new StringBuilder();
        for (Peer peer : discoveredPeers.values()) {
            sb.append(peer.userName)
                    .append(" @ ")
                    .append(peer.ip.getHostAddress())
                    .append(":")
                    .append(peer.port)
                    .append("\n");
        }
        binding.sampleText.setText(sb.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (jmDNS != null) {
            try {
                jmDNS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
