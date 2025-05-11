package com.example.p2p;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.p2p.databinding.ActivityMainBinding;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'p2p' library on application startup.
    private ActivityMainBinding binding;
    private static String TAG = "s";
    private NetworkInfo wifi;
    private PingService service;
    private UdpLanPinger pinger;
    private WifiLanAccessor i;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WifiManager man = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo info = man.getDhcpInfo();
        i = new WifiLanAccessor(info);
        wifi = new NetworkInfo(i);
        ArrayList<Peer> list = new ArrayList<>();
        try {
            pinger = new UdpLanPinger("DtlsUdpLanPinger", (char) 1000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        pinger.tryOpenSocket(4444);
        try {
            pinger.configureSocket((DatagramSocket socket) -> {
                socket.setBroadcast(true);
            });
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        service = new PingService(pinger, list);
        service.getAlivePeersAsync(new Network(wifi), "Mykola");
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        binding.sendButton.setOnClickListener(v -> sendUdpPing());
    }

//    private void sendUdpPing() {
//        runOnUiThread(() -> binding.resultText.setText("Result: Sending..."));
//
//        new Thread(() -> {
//            DatagramSocket socket = null;
//            try {
//                // Create socket
//                socket = new DatagramSocket();
//                socket.setBroadcast(true); // Enable broadcast (optional, for LAN discovery)
//                String localIp = socket.getLocalAddress().getHostAddress();
//                int localPort = socket.getLocalPort();
//                Log.i(TAG, "Socket opened on IP: " + localIp + ", port: " + localPort);
//
//                // Prepare packet
//                String message = "Hellooawodoawodaow";
//                byte[] data = message.getBytes();
//                InetAddress address = InetAddress.getByName("192.168.0.166");
//                int port = 1200;
//                DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
//
//                // Send packet
//                socket.send(packet);
//                Log.i(TAG, "Sent UDP packet from " + localIp + ":" + localPort + " to " + address.getHostAddress() + ":" + port);
//
//                // Optional: Try to receive a response
//                byte[] buffer = new byte[1024];
//                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
//                //socket.setSoTimeout(2000); // 2-second timeout
//                socket.receive(response);
//                String responseStr = new String(response.getData(), 0, response.getLength());
//                Log.i(TAG, "Received: " + responseStr + " from " + response.getAddress().getHostAddress() + ":" + response.getPort());
//
//                runOnUiThread(() -> {
//                    binding.resultText.setText("Result: Sent successfully, received: " + responseStr);
//                    Toast.makeText(this, "Packet sent and response received", Toast.LENGTH_SHORT).show();
//                });
//            } catch (SocketException e) {
//                Log.e(TAG, "Failed to create socket: " + e.getMessage(), e);
//                runOnUiThread(() -> {
//                    binding.resultText.setText("Result: Failed (Socket error)");
//                    Toast.makeText(this, "Socket error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                });
//            } catch (UnknownHostException e) {
//                Log.e(TAG, "Invalid IP address: " + e.getMessage(), e);
//                runOnUiThread(() -> {
//                    binding.resultText.setText("Result: Failed (Invalid IP)");
//                    Toast.makeText(this, "Invalid IP: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                });
//            } catch (IOException e) {
//                Log.e(TAG, "Failed to send/receive packet: " + e.getMessage(), e);
//                runOnUiThread(() -> {
//                    binding.resultText.setText("Result: Failed (IO error: " + e.getMessage() + ")");
//                    Toast.makeText(this, "IO error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                });
//            } finally {
//                if (socket != null && !socket.isClosed()) {
//                    socket.close();
//                    Log.i(TAG, "Socket closed");
//                }
//            }
//        }).start();
//    }
}