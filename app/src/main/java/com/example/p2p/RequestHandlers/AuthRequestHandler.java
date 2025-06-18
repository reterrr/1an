package com.example.p2p.RequestHandlers;

import android.util.Log;

import com.example.p2p.AuthPeerRepository;
import com.example.p2p.AuthService;
import com.example.p2p.Client;
import com.example.p2p.CurrentUserManager;
import com.example.p2p.E2ETool;
import com.example.p2p.Model.NetworkInfo;
import com.example.p2p.Model.User;
import com.example.p2p.Request.AuthRequest;
import com.example.p2p.Request.Request;
import com.example.p2p.Request.Sender;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class AuthRequestHandler implements RequestHandler<AuthRequest> {
    private final String tag = AuthRequest.class.toString();

    @Override
    public void handle(AuthRequest authRequest) throws Exception {
        AuthService.getInstance().handleReq(authRequest, new AuthService.Handler() {
            @Override
            public void onError(String userName) {
                Log.e(tag, "error on " + userName);
            }

            @Override
            public void onSuccess(User user) {
                AuthPeerRepository.getInstance().add(user);
                User cu = CurrentUserManager.getUser().user.getTarget();
                NetworkInfo info = user.networkInfo.getTarget();
                Client client = null;
                try {
                    client = Client.getInstance(InetAddress.getByName(info.ip), info.port);
                } catch (UnknownHostException e) {

                }
                String publicKey = E2ETool.getPublicKey();

                AuthRequest request = null;
                try {
                    request = new AuthRequest(Sender.fromUser(cu), E2ETool.computeFingerprint(publicKey), publicKey);
                } catch (UnknownHostException ignored) {

                }

                try {
                    client.send(Request.create("/auth/response", request));
                } catch (JsonProcessingException e) {
                    Log.e(tag, "Couldn't serialize auth request for " + info.ip);
                }

                client.close();
            }
        });
    }

    @Override
    public Class<AuthRequest> getRequestType() {
        return AuthRequest.class;
    }
}
