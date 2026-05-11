package com.example.betlink.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.betlink.R;
import com.example.betlink.data.SessionManager;
import com.example.betlink.databinding.ActivitySignupBinding;
import com.example.betlink.host.HostDashboardActivity;
import com.example.betlink.traveler.SearchActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class SignUpActivity extends AppCompatActivity {

    private AuthViewModel viewModel;
    private ActivitySignupBinding binding;
    private Uri selectedProfileImage;

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedProfileImage = uri;
                    binding.imageProfilePreview.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        binding.signupContent.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));

        // Setup Role Spinner
        String[] roles = {"Traveler", "Host"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.spinner_item_betlink, roles);
        adapter.setDropDownViewResource(R.layout.spinner_item_betlink);
        binding.spinnerRoleSelect.setAdapter(adapter);

        // Observers
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressSignup.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.buttonSignUpFinish.setEnabled(!isLoading);
            binding.buttonSignUpFinish.setText(isLoading ? "Signing up..." : getString(R.string.sign_up));
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getAuthSuccess().observe(this, response -> {
            if (response == null) return;

            String token = response.getAccessToken();

            if (token == null || token.isEmpty()) {
                // Email confirmation is required — Supabase created the user but
                // won't issue a token until the email is verified.
                Toast.makeText(this,
                    "Account created! Please check your email to confirm your account, then log in.",
                    Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, LoginActivity.class));
                finishAffinity();
                return;
            }

            if (response.getUser() != null) {
                // Email confirmation is OFF — we got a token, auto-login immediately.
                String userId  = response.getUser().getId();
                String email   = response.getUser().getEmail();
                String phone   = buildPhoneNumber();
                String fullName = binding.inputFullName.getText().toString().trim();
                String role    = binding.spinnerRoleSelect.getSelectedItem().toString();

                SessionManager.getInstance().saveSession(token, userId, email, phone, fullName, role);
                persistSelectedProfileImage();

                if ("Host".equalsIgnoreCase(role)) {
                    startActivity(new Intent(this, HostDashboardActivity.class));
                } else {
                    startActivity(new Intent(this, SearchActivity.class));
                }
                finishAffinity();
            }
        });

        // Click Listeners
        binding.cardProfilePhoto.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        binding.buttonSignUpFinish.setOnClickListener(v -> {
            String fullName = binding.inputFullName.getText().toString().trim();
            String email = binding.inputEmail.getText().toString().trim();
            String phone = buildPhoneNumber();
            String password = binding.inputPassword.getText().toString();
            String confirmPassword = binding.inputConfirmPassword.getText().toString();
            String role = binding.spinnerRoleSelect.getSelectedItem().toString();

            if (binding.inputPhone.getText().toString().trim().length() < 7) {
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!binding.checkboxTerms.isChecked()) {
                Toast.makeText(this, "Please accept the terms to continue", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.signUp(email, phone, password, fullName, role);
        });
    }

    private String buildPhoneNumber() {
        String code = binding.inputCountryCode.getText().toString().trim();
        String phone = binding.inputPhone.getText().toString().trim();
        if (phone.startsWith("+")) {
            return phone;
        }
        if (phone.startsWith("0")) {
            phone = phone.substring(1);
        }
        return code + phone;
    }

    private void persistSelectedProfileImage() {
        if (selectedProfileImage == null) {
            return;
        }
        try (InputStream inputStream = getContentResolver().openInputStream(selectedProfileImage)) {
            if (inputStream == null) {
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
            SessionManager.getInstance().saveProfileImageUri(Uri.fromFile(file).toString());
        } catch (Exception ignored) {
        }
    }
}
