package com.example.betlink.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.betlink.R;
import com.example.betlink.data.SessionManager;
import com.example.betlink.databinding.ActivityLoginBinding;
import com.example.betlink.host.HostDashboardActivity;
import com.example.betlink.traveler.SearchActivity;

public class LoginActivity extends AppCompatActivity {

    public static final String EXTRA_ROLE_HINT = "extra_role_hint";
    public static final String EXTRA_TRAVELER_NAME = "extra_traveler_name";

    private AuthViewModel viewModel;
    private ActivityLoginBinding binding;
    private static final String PREF_AUTH = "betlink_auth_preferences";
    private static final String KEY_REMEMBER = "remember_identifier";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        binding.loginContent.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        String remembered = getSharedPreferences(PREF_AUTH, MODE_PRIVATE).getString(KEY_REMEMBER, null);
        if (remembered != null) {
            binding.inputEmail.setText(remembered);
            binding.checkboxRememberMe.setChecked(true);
        }

        // Setup Observers
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressLogin.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.buttonContinue.setEnabled(!isLoading);
            binding.buttonSignUp.setEnabled(!isLoading);
            binding.buttonForgotPassword.setEnabled(!isLoading);
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                binding.textError.setText(error);
                binding.textError.setVisibility(View.VISIBLE);
            } else {
                binding.textError.setVisibility(View.GONE);
            }
        });

        viewModel.getInfoMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getAuthSuccess().observe(this, response -> {
            if (response != null && response.getUser() != null) {
                // Save session
                String token = response.getAccessToken();
                String userId = response.getUser().getId();
                String email = response.getUser().getEmail();
                String phone = response.getUser().getPhone();
                String fullName = response.getUser().getFullName();
                String role = response.getUser().getRole();

                // Handle missing full_name/role gracefully
                if (fullName == null) fullName = "Traveler";
                if (role == null) role = "Traveler";

                SessionManager.getInstance().saveSession(token, userId, email, phone, fullName, role);

                // Navigate based on role
                if ("Host".equalsIgnoreCase(role)) {
                    startActivity(new Intent(this, HostDashboardActivity.class));
                } else {
                    startActivity(new Intent(this, SearchActivity.class));
                }
                finishAffinity(); // Clear backstack so user can't navigate back to login
            }
        });

        // Setup Click Listeners
        binding.buttonContinue.setOnClickListener(v -> {
            // Hide error
            binding.textError.setVisibility(View.GONE);
            
            String email = binding.inputEmail.getText().toString().trim();
            String password = binding.inputPassword.getText().toString();
            rememberIdentifierIfNeeded(email);
            viewModel.login(email, password);
        });

        binding.buttonForgotPassword.setOnClickListener(v -> showForgotPasswordDialog());
        binding.buttonGoogle.setOnClickListener(v ->
                Toast.makeText(this, "Google login placeholder for the MVP.", Toast.LENGTH_SHORT).show());
        binding.buttonPhone.setOnClickListener(v ->
                Toast.makeText(this, "Enter your +251 phone number in the first field.", Toast.LENGTH_LONG).show());

        binding.buttonSignUp.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });
    }

    private void showForgotPasswordDialog() {
        EditText emailInput = new EditText(this);
        emailInput.setHint("Email address");
        emailInput.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailInput.setText(binding.inputEmail.getText());
        emailInput.setPadding(32, 16, 32, 16);

        new AlertDialog.Builder(this)
                .setTitle("Reset password")
                .setMessage("Enter your account email and BetLink will send reset instructions.")
                .setView(emailInput)
                .setPositiveButton("Send", (dialog, which) ->
                        viewModel.recoverPassword(emailInput.getText().toString().trim()))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void rememberIdentifierIfNeeded(String identifier) {
        SharedPreferences.Editor editor = getSharedPreferences(PREF_AUTH, MODE_PRIVATE).edit();
        if (binding.checkboxRememberMe.isChecked()) {
            editor.putString(KEY_REMEMBER, identifier);
        } else {
            editor.remove(KEY_REMEMBER);
        }
        editor.apply();
    }
}
