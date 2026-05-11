package com.example.betlink.common;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.betlink.R;
import com.example.betlink.data.SessionManager;
import com.example.betlink.host.HostDashboardActivity;
import com.example.betlink.traveler.SearchActivity;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION_MS = 900L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            
            // Check if user is already logged in
            if (SessionManager.getInstance().isLoggedIn()) {
                String role = SessionManager.getInstance().getUserRole();
                if ("Host".equalsIgnoreCase(role)) {
                    startActivity(new Intent(SplashActivity.this, HostDashboardActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, SearchActivity.class));
                }
            } else {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
            finish();

        }, SPLASH_DURATION_MS);
    }
}
