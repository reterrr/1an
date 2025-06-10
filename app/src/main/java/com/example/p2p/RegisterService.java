package com.example.p2p;

import androidx.annotation.NonNull;

import com.example.p2p.Model.CurrentUser;
import com.example.p2p.Model.CurrentUser_;
import com.example.p2p.Model.NetworkInfo;
import com.example.p2p.Model.User;
import com.example.p2p.Model.User_;
import com.example.p2p.auth.RegisterCode;
import com.example.p2p.auth.RegisterDto;

import org.mindrot.jbcrypt.BCrypt;

import java.util.Map;

import io.objectbox.Box;
import io.objectbox.query.QueryBuilder;

public class RegisterService {
    private final static RegisterService instance = new RegisterService();
    private final static Map<RegisterCode, String> map = Map.of(
            RegisterCode.SUCCESS, "Success",
            RegisterCode.EXISTS, "Already exists",
            RegisterCode.FAILED, "Failed"
    );

    private final static Map<RegisterDtoCode, String> dtoMap = Map.of(
            RegisterDtoCode.NOT_EMPTY, "Both fields cant be empty",
            RegisterDtoCode.CHARS, "In both fields use only a-Z, 0-9, #, !, $",
            RegisterDtoCode.PASSWORD_LENGTH, "Password must be at least 8 character long",
            RegisterDtoCode.CONFIRM_NOT_MATCH, "Confirm password and password don't match"
    );

    public interface Validate {
        void onValid(RegisterDto dto);

        void onError(String message);
    }

    private RegisterService() {
    }

    public static RegisterService getInstance() {
        return instance;
    }

    public String register(@NonNull RegisterDto dto) {
        return map.get(registerBase(dto));
    }

    private RegisterCode registerBase(@NonNull RegisterDto dto) {
        Box<User> userBox = ObjectBox.get().boxFor(User.class);
        Box<CurrentUser> currentUserBox = ObjectBox.get().boxFor(CurrentUser.class);

        QueryBuilder<CurrentUser> builder = currentUserBox.query();
        builder.link(CurrentUser_.user)
                .equal(User_.username, dto.username, QueryBuilder.StringOrder.CASE_SENSITIVE);

        CurrentUser existing = builder.build().findFirst();

        if (existing != null) {
            return RegisterCode.EXISTS;
        }

        String hashed = BCrypt.hashpw(dto.password, BCrypt.gensalt());

        NetworkInfo networkInfo = NetworkResourceManager.getDeviceNetworkInfo();
        // FIXME: 6/10/25 network
        User newUser = new User();
        newUser.username = dto.username;
        newUser.networkInfo.setTarget(networkInfo);
        userBox.put(newUser);

        CurrentUser cu = new CurrentUser();
        cu.user.setTarget(newUser);
        cu.passwordHash = hashed;
        currentUserBox.put(cu);

        return RegisterCode.SUCCESS;
    }

    public void validate(@NonNull RegisterDto registerDto, Validate error) {
        String regex = "[A-z0-9#!]+";

        if (registerDto.password.isEmpty() || registerDto.username.isEmpty()) {
            error.onError(dtoMap.get(RegisterDtoCode.NOT_EMPTY));

            return;
        }

        if (registerDto.password.length() < 8) {
            error.onError(dtoMap.get(RegisterDtoCode.PASSWORD_LENGTH));

            return;
        }

        if (!registerDto.username.matches(regex) &&
                !registerDto.password.matches(regex)) {
            error.onError(dtoMap.get(RegisterDtoCode.CHARS));

            return;
        }

        if (!registerDto.password.equals(registerDto.confirmPassword)) {
            error.onError(dtoMap.get(RegisterDtoCode.CONFIRM_NOT_MATCH));
        }

        error.onValid(registerDto);
    }
}
