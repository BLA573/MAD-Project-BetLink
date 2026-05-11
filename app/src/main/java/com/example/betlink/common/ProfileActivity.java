package com.example.betlink.common;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.betlink.data.SessionManager;
import com.example.betlink.databinding.ActivityProfileBinding;
import com.example.betlink.host.HostDashboardActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    saveProfileImage(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String name = SessionManager.getInstance().getUserName();
        String email = SessionManager.getInstance().getUserEmail();
        String phone = SessionManager.getInstance().getUserPhone();
        binding.profileName.setText(name);
        binding.profileEmail.setText(email != null ? email : "");
        binding.profilePhone.setText(phone != null ? phone : "Not added yet");
        String profileImageUri = SessionManager.getInstance().getProfileImageUri();
        if (profileImageUri != null) {
            binding.profileImage.setImageURI(Uri.parse(profileImageUri));
        }

        String role = SessionManager.getInstance().getUserRole();
        boolean isHost = "Host".equalsIgnoreCase(role);
        binding.profileRoleBadge.setText(isHost ? "Verified Host" : "Traveler");
        binding.textHostingPreferences.setVisibility(isHost ? View.VISIBLE : View.GONE);
        binding.cardHostingPreferences.setVisibility(isHost ? View.VISIBLE : View.GONE);
        binding.buttonSwitchToHost.setVisibility(isHost ? View.VISIBLE : View.GONE);

        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.buttonProfilePhoto.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

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

    private void saveProfileImage(Uri sourceUri) {
        try (InputStream inputStream = getContentResolver().openInputStream(sourceUri)) {
            if (inputStream == null) {
                Toast.makeText(this, "Could not open image", Toast.LENGTH_SHORT).show();
                return;
            }

            File file = new File(getFilesDir(), "profile_image.jpg");
            try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
            }

            Uri savedUri = Uri.fromFile(file);
            SessionManager.getInstance().saveProfileImageUri(savedUri.toString());
            binding.profileImage.setImageURI(savedUri);
            Toast.makeText(this, "Profile photo saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Profile photo could not be saved", Toast.LENGTH_SHORT).show();
        }
    }
}
