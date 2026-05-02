package com.example.betlink.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.betlink.R;
import com.example.betlink.data.SessionManager;
import com.example.betlink.databinding.ActivityLoginBinding;
import com.example.betlink.host.HostOnboardingActivity;
import com.example.betlink.traveler.SearchActivity;

public class LoginActivity extends AppCompatActivity {

    public static final String EXTRA_ROLE_HINT = "extra_role_hint";
    public static final String EXTRA_TRAVELER_NAME = "extra_traveler_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String[] roles = new String[]{"Traveler", "Host"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item_betlink,
                roles
        );
        adapter.setDropDownViewResource(R.layout.spinner_item_betlink);
        binding.spinnerRole.setAdapter(adapter);

        String roleHint = getIntent().getStringExtra(EXTRA_ROLE_HINT);
        if ("Host".equals(roleHint)) {
            binding.spinnerRole.setSelection(1);
        }

        binding.buttonContinue.setOnClickListener(v -> {
            String fullName = binding.inputName.getText().toString().trim();
            String role = String.valueOf(binding.spinnerRole.getSelectedItem());

            if (TextUtils.isEmpty(fullName)) {
                Toast.makeText(this, R.string.name_required, Toast.LENGTH_SHORT).show();
                return;
            }

            // Start session
            SessionManager.getInstance().startSession(fullName, role);

            if ("Host".equals(role)) {
                Intent intent = new Intent(this, HostOnboardingActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
            }
            finish();
        });
    }
}
