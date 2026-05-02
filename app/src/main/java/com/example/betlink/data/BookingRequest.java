package com.example.betlink.data;

public class BookingRequest {
    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_APPROVED = "Approved";
    public static final String STATUS_REJECTED = "Rejected";

    private final int id;
    private final int listingId;
    private final String travelerName;
    private final String checkInDate;
    private final String checkOutDate;
    private String status;

    public BookingRequest(
            int id,
            int listingId,
            String travelerName,
            String checkInDate,
            String checkOutDate,
            String status
    ) {
        this.id = id;
        this.listingId = listingId;
        this.travelerName = travelerName;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public int getListingId() {
        return listingId;
    }

    public String getTravelerName() {
        return travelerName;
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

