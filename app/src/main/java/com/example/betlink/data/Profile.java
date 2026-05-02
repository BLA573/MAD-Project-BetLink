package com.example.betlink.data;

import com.google.gson.annotations.SerializedName;

public class Profile {
    @SerializedName("id")
    private String id;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("role")
    private String role; // 'traveler' or 'host'

    @SerializedName("is_verified")
    private boolean isVerified;

    public Profile() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }
}
