package com.example.betlink.common;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.betlink.databinding.ActivityMainBinding;
import com.example.betlink.auth.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonStartTravelerFlow.setOnClickListener(v -> openLogin("Traveler"));
        binding.buttonStartHostFlow.setOnClickListener(v -> openLogin("Host"));
    }

    private void openLogin(String roleHint) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LoginActivity.EXTRA_ROLE_HINT, roleHint);
        startActivity(intent);
    }
}
