package com.example.betlink.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.betlink.auth.LoginActivity;
import com.example.betlink.databinding.ActivityMainBinding;

/**
 * Acts as the Welcome Screen (first screen users see if not logged in).
 * Contains the "Get Started" (Sign Up) and "Log In" buttons.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // We reuse the existing activity_main.xml layout but adjust it for Auth
        binding.buttonStartTravelerFlow.setText(com.example.betlink.R.string.sign_up);
        binding.buttonStartHostFlow.setText(com.example.betlink.R.string.log_in);

        binding.buttonStartTravelerFlow.setOnClickListener(v -> {
            startActivity(new Intent(this, com.example.betlink.auth.SignUpActivity.class));
        });
        
        binding.buttonStartHostFlow.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });
    }
}
