package com.example.betlink.data;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class AuthRequest {

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("password")
    private String password;

    /**
     * GoTrue REST API v1 expects user metadata directly under "data" at the root.
     * This is different from the JS client SDK which uses options.data.
     */
    @SerializedName("data")
    private Map<String, String> data;

    public AuthRequest(String identifier, String password) {
        if (identifier != null && identifier.contains("@")) {
            this.email = identifier;
        } else {
            this.phone = identifier;
        }
        this.password = password;
    }

    public AuthRequest(String email, String phone, String password, Map<String, String> userData) {
        this.email    = email;
        if (email == null || email.trim().isEmpty()) {
            this.phone = phone;
        }
        this.password = password;
        this.data     = userData;
    }
}
