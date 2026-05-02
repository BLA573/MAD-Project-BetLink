package com.example.betlink.host;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.betlink.databinding.ActivityAvailabilityCalendarBinding;

public class AvailabilityCalendarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAvailabilityCalendarBinding binding = ActivityAvailabilityCalendarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonSaveAvailability.setOnClickListener(v -> {
            // Logic to save availability (e.g., API call or database update)
            finish();
        });
    }
}
