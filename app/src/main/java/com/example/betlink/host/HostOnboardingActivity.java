package com.example.betlink.host;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.betlink.databinding.ActivityHostOnboardingBinding;
import com.example.betlink.common.ProfileActivity;

public class HostOnboardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityHostOnboardingBinding binding = ActivityHostOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonCreateFirstListing.setOnClickListener(v -> {
            // Direct to creating the first listing
            startActivity(new Intent(this, AddListingActivity.class));
            finish();
        });

        binding.buttonSkipOnboarding.setOnClickListener(v -> {
            // Go directly to the control center
            startActivity(new Intent(this, HostDashboardActivity.class));
            finish();
        });

        binding.buttonSetupProfile.setOnClickListener(v -> {
            // Go to profile setup
            startActivity(new Intent(this, ProfileActivity.class));
        });
    }
}
