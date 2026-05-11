package com.example.betlink.data;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("token_type")
    private String tokenType;

    @SerializedName("user")
    private User user;

    public String getAccessToken() { return accessToken; }
    public String getTokenType()   { return tokenType; }
    public User   getUser()        { return user; }

    public static class User {
        @SerializedName("id")
        private String id;

        @SerializedName("email")
        private String email;

        @SerializedName("phone")
        private String phone;

        /** Supabase stores custom metadata in user_metadata when signing up */
        @SerializedName("user_metadata")
        private UserMetadata userMetadata;

        public String getId()    { return id; }
        public String getEmail() { return email; }
        public String getPhone() { return phone != null ? phone : getMetadataPhone(); }

        public String getFullName() {
            return userMetadata != null ? userMetadata.fullName : null;
        }

        public String getRole() {
            return userMetadata != null ? userMetadata.role : null;
        }

        private String getMetadataPhone() {
            return userMetadata != null ? userMetadata.phone : null;
        }
    }

    public static class UserMetadata {
        @SerializedName("full_name")
        public String fullName;

        @SerializedName("role")
        public String role;

        @SerializedName("phone")
        public String phone;
    }
}
