package com.example.p2p;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.p2p.Request.Request;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    private static final String tag = Client.class.toString();
    private final Socket socket;
    private final DataOutputStream out;

    private Client(@NonNull Socket socket) throws IOException {
        this.socket = socket;
        this.out = new DataOutputStream(socket.getOutputStream());
    }

    public static Client getInstance(InetAddress address, int port) {
        try {
            Socket socket = new Socket(address, port);

            return new Client(socket);
        } catch (IOException e) {
            Log.e(tag, "was unable to open connection with " + address + " " + port);

            return null;
        }
    }

    public void send(Request request) {
        byte[] data = request.toBytes();

        try {
            out.writeInt(data.length);
            out.write(data);
            out.flush();
        } catch (IOException e) {
            Log.e(tag, "Error sending request on socket", e);
        }
    }

    public void close() {
        try {
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            Log.e(tag, "Error closing client", e);
        }
    }
}
