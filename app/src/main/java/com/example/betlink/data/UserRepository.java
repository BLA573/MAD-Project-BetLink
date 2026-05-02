package com.example.betlink.data;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Single source of truth for authentication operations.
 * Bridges SupabaseService (Retrofit) with the rest of the app.
 */
public class UserRepository {

    private final SupabaseService service;
    private final String          anonKey;

    public UserRepository() {
        this.service = SupabaseClient.getService();
        this.anonKey = SupabaseClient.getAnonKey();
    }

    // -------------------------------------------------------------------------
    // Callback interface
    // -------------------------------------------------------------------------

    public interface AuthCallback {
        void onSuccess(AuthResponse response);
        void onError(String message);
    }

    // -------------------------------------------------------------------------
    // Login
    // -------------------------------------------------------------------------

    public void login(String email, String password, AuthCallback callback) {
        AuthRequest request = new AuthRequest(email, password);
        service.login(anonKey, request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call,
                                   @NonNull Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String msg = parseError(response.code());
                    callback.onError(msg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // -------------------------------------------------------------------------
    // Sign-up
    // -------------------------------------------------------------------------

    public void signUp(String email, String password,
                       String fullName, String role,
                       AuthCallback callback) {

        Map<String, String> userData = new HashMap<>();
        userData.put("full_name", fullName);
        userData.put("role",      role);

        AuthRequest request = new AuthRequest(email, password, userData);
        service.signUp(anonKey, request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call,
                                   @NonNull Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String msg = parseError(response.code());
                    callback.onError(msg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private String parseError(int code) {
        switch (code) {
            case 400: return "Invalid credentials. Check your email and password.";
            case 422: return "Email already registered or invalid format.";
            case 429: return "Too many requests. Please wait a moment.";
            default:  return "Server error (" + code + "). Please try again.";
        }
    }
}
