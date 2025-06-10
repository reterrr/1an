package com.example.p2p.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.p2p.RegisterService;
import com.example.p2p.auth.RegisterDto;
import com.example.p2p.databinding.ActivityRegisterBinding;

public class RegisterActivity extends Activity {
    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.registerButton.setOnClickListener(v -> {
            RegisterDto dto = new RegisterDto(
                    binding.registerUsername.getText().toString(),
                    binding.registerPassword.getText().toString(),
                    binding.registerConfirmPassword.getText().toString()
            );

            RegisterService.getInstance().validate(dto, new RegisterService.Validate() {
                @Override
                public void onValid(RegisterDto dto) {
                    String message = RegisterService.getInstance().register(dto);
                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        });

        binding.goToLoginButton.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });
    }
}
