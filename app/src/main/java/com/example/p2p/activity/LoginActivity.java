package com.example.p2p.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.p2p.LoginService;
import com.example.p2p.auth.LoginDto;
import com.example.p2p.databinding.ActivityLoginBinding;

public class LoginActivity extends Activity {
    private ActivityLoginBinding binding;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupListeners();
    }

    private void setupListeners() {
        binding.eyeIcon.setOnClickListener(v -> {
            if (isPasswordVisible) {
                binding.passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                isPasswordVisible = false;
            } else {
                binding.passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                isPasswordVisible = true;
            }

            binding.passwordField.setSelection(binding.passwordField.getText().length());
        });

        binding.loginButton.setOnClickListener(v -> {
            LoginDto dto = new LoginDto(binding.username.getText().toString(), binding.passwordField.getText().toString());
            LoginService.getTarget().login(dto, new LoginService.Login() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, PeerListActivity.class));
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        });

        binding.registerText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
