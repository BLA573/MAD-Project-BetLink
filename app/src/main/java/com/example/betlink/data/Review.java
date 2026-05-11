package com.example.betlink.data;

import com.google.gson.annotations.SerializedName;

public class Review {
    @SerializedName("id")
    private String id;

    @SerializedName("listing_id")
    private String listingId;

    @SerializedName("traveler_id")
    private String travelerId;

    @SerializedName("rating")
    private int rating;

    @SerializedName("comment")
    private String comment;

    @SerializedName("created_at")
    private String createdAt;

    public Review() {
    }

    public Review(String listingId, String travelerId, int rating, String comment) {
        this.listingId = listingId;
        this.travelerId = travelerId;
        this.rating = rating;
        this.comment = comment;
    }

    public String getId() { return id; }
    public String getListingId() { return listingId; }
    public String getTravelerId() { return travelerId; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public String getCreatedAt() { return createdAt; }
}
