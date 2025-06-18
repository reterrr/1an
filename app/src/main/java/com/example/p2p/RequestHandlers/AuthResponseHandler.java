package com.example.p2p.RequestHandlers;

import com.example.p2p.Request.AuthResponse;

public class AuthResponseHandler implements RequestHandler<AuthResponse> {
    @Override
    public void handle(AuthResponse authResponse) throws Exception {
        int i = 1;
    }

    @Override
    public Class<AuthResponse> getRequestType() {
        return AuthResponse.class;
    }
}
