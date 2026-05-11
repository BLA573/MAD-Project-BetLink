package com.example.betlink.data;

import com.google.gson.annotations.SerializedName;

public class BookingRequest {
    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_APPROVED = "Approved";
    public static final String STATUS_REJECTED = "Rejected";

    @SerializedName("id")
    private String id;

    @SerializedName("listing_id")
    private String listingId;

    @SerializedName("traveler_id")
    private String travelerId;

    @SerializedName("check_in_date")
    private String checkInDate;

    @SerializedName("check_out_date")
    private String checkOutDate;

    @SerializedName("status")
    private String status;

    public BookingRequest() {
    }

    public BookingRequest(
            String listingId,
            String travelerId,
            String checkInDate,
            String checkOutDate,
            String status
    ) {
        this(null, listingId, travelerId, checkInDate, checkOutDate, status);
    }

    public BookingRequest(
            String id,
            String listingId,
            String travelerId,
            String checkInDate,
            String checkOutDate,
            String status
    ) {
        this.id = id;
        this.listingId = listingId;
        this.travelerId = travelerId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getListingId() {
        return listingId;
    }

    public String getTravelerId() {
        return travelerId;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
