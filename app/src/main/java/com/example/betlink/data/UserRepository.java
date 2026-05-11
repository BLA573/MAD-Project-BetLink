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

    public void signUp(String email, String phone, String password,
                       String fullName, String role,
                       AuthCallback callback) {

        Map<String, String> userData = new HashMap<>();
        userData.put("full_name", fullName);
        userData.put("role",      role);
        userData.put("phone",     phone);

        AuthRequest request = new AuthRequest(email, phone, password, userData);
        service.signUp(anonKey, request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call,
                                   @NonNull Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse body = response.body();
                    // Supabase returns a user object but null token when email confirmation is pending.
                    // We treat this as a partial success and still notify callback with the response.
                    callback.onSuccess(body);
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

    public void recoverPassword(String email, SimpleCallback callback) {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);

        service.recoverPassword(anonKey, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError(parseError(response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
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
            case 422: return "Email already registered or invalid email format.";
            case 429: return "Too many requests. Please wait a moment.";
            case 500: return "Sign-up failed (server error). If this persists, go to Supabase Dashboard → Authentication → Settings → Email Confirmations and disable 'Enable email confirmations'.";
            default:  return "Server error (" + code + "). Please try again.";
        }
    }

    public interface SimpleCallback {
        void onSuccess();
        void onError(String message);
    }
}
