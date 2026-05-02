package com.example.betlink.data;

import java.util.ArrayList;
import java.util.List;

public class Listing {
    private final int id;
    private final String title;
    private final String city;
    private final String landmark;
    private final int pricePerNight;
    private final boolean verified;
    private final String amenities;
    private final String description;
    private final String cancellationRule;
    private final float rating;
    private final String roomType;
    private final boolean available;
    private final List<String> images;

    public Listing(
            int id,
            String title,
            String city,
            String landmark,
            int pricePerNight,
            boolean verified,
            String amenities,
            String description,
            String cancellationRule,
            float rating,
            String roomType,
            boolean available
    ) {
        this(id, title, city, landmark, pricePerNight, verified, amenities, description, cancellationRule, rating, roomType, available, new ArrayList<>());
    }

    public Listing(
            int id,
            String title,
            String city,
            String landmark,
            int pricePerNight,
            boolean verified,
            String amenities,
            String description,
            String cancellationRule,
            float rating,
            String roomType,
            boolean available,
            List<String> images
    ) {
        this.id = id;
        this.title = title;
        this.city = city;
        this.landmark = landmark;
        this.pricePerNight = pricePerNight;
        this.verified = verified;
        this.amenities = amenities;
        this.description = description;
        this.cancellationRule = cancellationRule;
        this.rating = rating;
        this.roomType = roomType;
        this.available = available;
        this.images = images;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCity() {
        return city;
    }

    public String getLandmark() {
        return landmark;
    }

    public int getPricePerNight() {
        return pricePerNight;
    }

    public boolean isVerified() {
        return verified;
    }

    public String getAmenities() {
        return amenities;
    }

    public String getDescription() {
        return description;
    }

    public String getCancellationRule() {
        return cancellationRule;
    }

    public float getRating() {
        return rating;
    }

    public String getRoomType() {
        return roomType;
    }

    public boolean isAvailable() {
        return available;
    }

    public List<String> getImages() {
        return images;
    }
}
