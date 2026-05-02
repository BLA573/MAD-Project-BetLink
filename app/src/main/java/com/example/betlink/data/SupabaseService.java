package com.example.betlink.data;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface SupabaseService {

    /** Supabase Auth – Sign up with email + password */
    @POST("auth/v1/signup")
    Call<AuthResponse> signUp(
        @Header("apikey") String apiKey,
        @Body AuthRequest request
    );

    /** Supabase Auth – Login with email + password */
    @POST("auth/v1/token?grant_type=password")
    Call<AuthResponse> login(
        @Header("apikey") String apiKey,
        @Body AuthRequest request
    );
}
