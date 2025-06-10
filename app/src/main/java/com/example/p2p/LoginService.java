package com.example.p2p;

import androidx.annotation.NonNull;

import com.example.p2p.auth.LoginCode;
import com.example.p2p.auth.LoginDto;

import java.util.Map;

public class LoginService {
    private LoginService() {
    }

    public interface Login {
        void onSuccess(String message);

        void onError(String message);
    }

    private final static LoginService instance = new LoginService();
    private final static Map<LoginCode, String> map = Map.of(
            LoginCode.SUCCESS, "Success",
            LoginCode.INVALID_CREDENTIALS, "Invalid credentials",
            LoginCode.NO_SUCH_USER, "No such user"
    );

    public static LoginService getTarget() {
        return instance;
    }

    public void login(@NonNull LoginDto dto, Login login) {
        LoginCode code = CurrentUserManager.login(dto);

        if (code == LoginCode.SUCCESS)
            login.onSuccess(map.get(code));

        login.onError(map.get(code));
    }
}
