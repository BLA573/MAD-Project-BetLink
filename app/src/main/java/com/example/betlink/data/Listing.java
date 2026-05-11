package com.example.betlink.data;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class Listing {

    @SerializedName("id")
    private String id;

    @SerializedName("host_id")
    private String hostId;

    @SerializedName("title")
    private String title;

    @SerializedName("city")
    private String city;

    @SerializedName("landmark")
    private String landmark;

    @SerializedName("price_per_night")
    private int pricePerNight;

    @SerializedName("is_verified")
    private boolean verified;

    @SerializedName("amenities")
    private String amenities;

    @SerializedName("description")
    private String description;

    @SerializedName("cancellation_rule")
    private String cancellationRule;

    @SerializedName("avg_rating")
    private float avgRating;

    @SerializedName("room_type")
    private String roomType;

    @SerializedName("image_urls")
    private List<String> imageUrls;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    // Default constructor for Gson
    public Listing() {
        this.imageUrls = new ArrayList<>();
    }

    public Listing(String hostId, String title, String city, String landmark, int pricePerNight,
                   String amenities, String description, String cancellationRule, String roomType, List<String> imageUrls) {
        this.hostId = hostId;
        this.title = title;
        this.city = city;
        this.landmark = landmark;
        this.pricePerNight = pricePerNight;
        this.amenities = amenities;
        this.description = description;
        this.cancellationRule = cancellationRule;
        this.roomType = roomType;
        this.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
    }

    public Listing(String id, String hostId, String title, String city, String landmark, int pricePerNight,
                   boolean verified, String amenities, String description, String cancellationRule, float avgRating, String roomType, List<String> imageUrls) {
        this.id = id;
        this.hostId = hostId;
        this.title = title;
        this.city = city;
        this.landmark = landmark;
        this.pricePerNight = pricePerNight;
        this.verified = verified;
        this.amenities = amenities;
        this.description = description;
        this.cancellationRule = cancellationRule;
        this.avgRating = avgRating;
        this.roomType = roomType;
        this.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
    }

    // Getters
    public String getId() { return id; }
    public String getHostId() { return hostId; }
    public String getTitle() { return title; }
    public String getCity() { return city; }
    public String getLandmark() { return landmark; }
    public int getPricePerNight() { return pricePerNight; }
    public boolean isVerified() { return verified; }
    public String getAmenities() { return amenities; }
    public String getDescription() { return description; }
    public String getCancellationRule() { return cancellationRule; }
    public float getAvgRating() { return avgRating; }
    public String getRoomType() { return roomType; }
    public List<String> getImageUrls() { return imageUrls; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }

    // Setters (useful for mock or local modifications)
    public void setId(String id) { this.id = id; }
}
