package com.example.betlink.common;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.betlink.data.SessionManager;
import com.example.betlink.databinding.ActivityProfileBinding;
import com.example.betlink.host.HostDashboardActivity;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String name = SessionManager.getInstance().getUserName();
        binding.profileName.setText(name);
        binding.profileEmail.setText(name.toLowerCase().replace(" ", ".") + "@example.com");

        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.buttonSwitchToHost.setOnClickListener(v -> {
            startActivity(new Intent(this, HostDashboardActivity.class));
            finish();
        });

        binding.buttonLogout.setOnClickListener(v -> {
            SessionManager.getInstance().clear();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        View.OnClickListener comingSoon = v -> 
            Toast.makeText(this, "Feature coming soon!", Toast.LENGTH_SHORT).show();

        binding.profileName.setOnClickListener(comingSoon);
        // Assuming we add IDs to the preference rows or just bind the container
        // For now, let's just make the whole containers clickable for the effect
        ((View)binding.profileEmail.getParent()).setOnClickListener(comingSoon);
    }
}
