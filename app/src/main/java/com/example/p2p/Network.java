package com.example.p2p;

import androidx.annotation.NonNull;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Network implements Iterable<InetAddress> {

    private final NetworkInfo info;

    public Network(NetworkInfo info) {
        this.info = info;
    }

    @NonNull
    @Override
    public Iterator<InetAddress> iterator() {
        return new Iterator<>() {
            private final int deviceIp = inetToInt(info.deviceIp);
            private final int broadcastIp = inetToInt(info.broadcastIp);
            private final int gatewayIp = inetToInt(info.gatewayIp);
            private final int start = inetToInt(info.hostMin);
            private final int end = inetToInt(info.hostMax);
            private int current = start;

            @Override
            public boolean hasNext() {
                return current <= end;
            }

            @Override
            public InetAddress next() {
                if (!hasNext()) throw new NoSuchElementException();

                while (hasNext()) {
                    int candidate = current++;
                    if (candidate != broadcastIp && candidate != deviceIp && candidate != gatewayIp) {
                        try {
                            return intToInet(candidate);
                        } catch (UnknownHostException e) {
                            throw new RuntimeException("Invalid IP conversion", e);
                        }
                    }
                }

                throw new NoSuchElementException();
            }
        };
    }

    private static int inetToInt(InetAddress inet) {
        byte[] bytes = inet.getAddress();
        return ((bytes[0] & 0xFF) << 24)
                | ((bytes[1] & 0xFF) << 16)
                | ((bytes[2] & 0xFF) << 8)
                | (bytes[3] & 0xFF);
    }

    private static InetAddress intToInet(int ip) throws UnknownHostException {
        byte[] bytes = new byte[]{
                (byte) ((ip >> 24) & 0xFF),
                (byte) ((ip >> 16) & 0xFF),
                (byte) ((ip >> 8) & 0xFF),
                (byte) (ip & 0xFF)
        };
        return InetAddress.getByAddress(bytes);
    }
}
