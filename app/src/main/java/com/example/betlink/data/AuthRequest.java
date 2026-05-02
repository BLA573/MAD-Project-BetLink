package com.example.betlink.data;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class AuthRequest {

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    /**
     * Supabase accepts arbitrary user metadata on signup under "data".
     * We store full_name and role here.
     */
    @SerializedName("data")
    private Map<String, String> userData;

    public AuthRequest(String email, String password) {
        this.email    = email;
        this.password = password;
    }

    public AuthRequest(String email, String password, Map<String, String> userData) {
        this.email    = email;
        this.password = password;
        this.userData = userData;
    }
}
