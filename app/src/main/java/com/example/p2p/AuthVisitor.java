package com.example.p2p;

import android.util.Log;

import com.example.p2p.Model.User;
import com.example.p2p.Request.AuthRequest;
import com.example.p2p.Request.Request;
import com.example.p2p.Request.Sender;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.net.UnknownHostException;


public class AuthVisitor implements Visitor<Peer> {
    private final String tag = AuthVisitor.class.toString();
    private final User currentUser = CurrentUserManager.getUser().user.getTarget();

    @Override
    public void visit(Peer peer) {
        new Thread(() -> {
            var client = Client.getInstance(peer.ip, peer.port);
            String publicKey = E2ETool.getPublicKey();
//            if (client == null) return;

            AuthRequest request = null;
            try {
                request = new AuthRequest(Sender.fromUser(currentUser), E2ETool.computeFingerprint(publicKey), publicKey);
            } catch (UnknownHostException ignored) {

            }

            try {
                client.send(Request.create("/auth/request", request));
            } catch (JsonProcessingException e) {
                Log.e(tag, "Couldn't serialize auth request for " + peer.ip);
            }

            client.close();
        }).start();

    }
}
