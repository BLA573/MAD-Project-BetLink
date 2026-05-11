package com.example.betlink.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class MockRepository {
    private static MockRepository instance;

    private final List<Listing> listings = new ArrayList<>();
    private final List<BookingRequest> bookingRequests = new ArrayList<>();
    private final List<MessageThread> messages = new ArrayList<>();
    private final AtomicInteger listingIdCounter = new AtomicInteger(5);
    private final AtomicInteger bookingIdCounter = new AtomicInteger(1000);

    private MockRepository() {
        seedListings();
        seedBookings();
        seedMessages();
    }

    public static synchronized MockRepository getInstance() {
        if (instance == null) {
            instance = new MockRepository();
        }
        return instance;
    }

    private void seedListings() {
        listings.add(new Listing(
                "1", "host-1",
                "Campus Budget Room",
                "Hawassa",
                "Near Hawassa University",
                700,
                true,
                "WiFi, Shared kitchen, Study desk",
                "Simple, clean room ideal for exam and placement stays. Located in a quiet neighborhood perfect for students.",
                "Free cancellation up to 12 hours before check-in.",
                4.8f,
                "Single Room",
                new ArrayList<>()
        ));
        listings.add(new Listing(
                "2", "host-2",
                "Transit Rest House",
                "Adama",
                "2 minutes from bus station",
                500,
                false,
                "24/7 desk, Hot shower, Near transport",
                "Designed for late-night transit travelers needing quick rest. Affordable and safe with basic amenities.",
                "50% refund for cancellations up to 6 hours before check-in.",
                4.2f,
                "Studio",
                new ArrayList<>()
        ));
        listings.add(new Listing(
                "3", "host-3",
                "Verified Family Guesthouse",
                "Bahir Dar",
                "Close to city center",
                1200,
                true,
                "Private bathroom, Breakfast, Parking",
                "Safe short-stay option for families and visitors. Spacious rooms with beautiful lake views nearby.",
                "Full refund up to 24 hours before check-in.",
                4.9f,
                "Full House",
                new ArrayList<>()
        ));
    }

    private void seedBookings() {
        createBookingRequest("1", "traveler-1", "2026-05-12", "2026-05-14");
        createBookingRequest("3", "traveler-2", "2026-06-01", "2026-06-05");
    }

    private void seedMessages() {
        messages.add(new MessageThread(
                "Solomon K.",
                "Is the WiFi fast enough for video calls?",
                "10:45 AM",
                2,
                "BR-1001",
                "Pending",
                Arrays.asList("Quick check about WiFi", "Is it fast enough for video calls?")
        ));
        messages.add(new MessageThread(
                "Martha S.",
                "Thank you for the wonderful stay!",
                "Yesterday",
                0,
                "BR-1002",
                "Completed",
                Arrays.asList("Just arrived!", "Thank you for the wonderful stay!")
        ));
        messages.add(new MessageThread(
                "Alex J.",
                "I have a question about the parking.",
                "Monday",
                1,
                "BR-1003",
                "Approved",
                Arrays.asList("Hello", "I have a question about the parking.")
        ));
    }

    public List<Listing> getListings() {
        return new ArrayList<>(listings);
    }

    public void addListing(Listing listing) {
        listings.add(0, listing);
    }

    public void updateListingPrice(String id, int newPrice) {
        Listing listing = getListingById(id);
        if (listing != null) {
            int index = listings.indexOf(listing);
            Listing updated = new Listing(
                listing.getId(), listing.getHostId(), listing.getTitle(), listing.getCity(), listing.getLandmark(),
                newPrice, listing.isVerified(), listing.getAmenities(), listing.getDescription(),
                listing.getCancellationRule(), listing.getAvgRating(), listing.getRoomType(),
                listing.getImageUrls()
            );
            listings.set(index, updated);
        }
    }

    public String getNextListingId() {
        return String.valueOf(listingIdCounter.incrementAndGet());
    }

    public List<Listing> searchListings(String query, int maxPrice, boolean verifiedOnly) {
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase(Locale.US);
        List<Listing> result = new ArrayList<>();

        for (Listing listing : listings) {
            boolean textMatch = normalizedQuery.isEmpty()
                    || listing.getCity().toLowerCase(Locale.US).contains(normalizedQuery)
                    || listing.getLandmark().toLowerCase(Locale.US).contains(normalizedQuery)
                    || listing.getTitle().toLowerCase(Locale.US).contains(normalizedQuery);
            boolean priceMatch = maxPrice <= 0 || listing.getPricePerNight() <= maxPrice;
            boolean verifiedMatch = !verifiedOnly || listing.isVerified();

            if (textMatch && priceMatch && verifiedMatch) {
                result.add(listing);
            }
        }

        return result;
    }

    public Listing getListingById(String listingId) {
        for (Listing listing : listings) {
            if (listingId.equals(listing.getId())) {
                return listing;
            }
        }
        return null;
    }

    public BookingRequest createBookingRequest(
            String listingId,
            String travelerId,
            String checkInDate,
            String checkOutDate
    ) {
        BookingRequest request = new BookingRequest(
                String.valueOf(bookingIdCounter.incrementAndGet()),
                listingId,
                travelerId,
                checkInDate,
                checkOutDate,
                BookingRequest.STATUS_PENDING
        );
        bookingRequests.add(0, request);
        return request;
    }

    public BookingRequest getBookingById(String bookingId) {
        for (BookingRequest request : bookingRequests) {
            if (request.getId().equals(bookingId)) {
                return request;
            }
        }
        return null;
    }

    public List<BookingRequest> getBookingRequests() {
        return new ArrayList<>(bookingRequests);
    }

    public void updateBookingStatus(String bookingId, String newStatus) {
        BookingRequest request = getBookingById(bookingId);
        if (request != null) {
            request.setStatus(newStatus);
        }
    }

    public List<MessageThread> getMessages() {
        return new ArrayList<>(messages);
    }

    public static class MessageThread {
        public String sender;
        public String lastMessage;
        public String time;
        public int unreadCount;
        public String bookingRef;
        public String bookingStatus;
        public List<String> conversation;

        public MessageThread(String sender, String lastMessage, String time, int unreadCount, String bookingRef, String bookingStatus, List<String> conversation) {
            this.sender = sender;
            this.lastMessage = lastMessage;
            this.time = time;
            this.unreadCount = unreadCount;
            this.bookingRef = bookingRef;
            this.bookingStatus = bookingStatus;
            this.conversation = conversation;
        }
    }

    public PerformanceStats getPerformanceStats() {
        return new PerformanceStats(1240, 42, 142500, "85%", "98%", "4.8", Arrays.asList(4.5f, 4.7f, 4.8f, 4.8f, 4.9f));
    }

    public static class PerformanceStats {
        public int totalViews;
        public int totalBookings;
        public int totalRevenue;
        public String occupancyRate;
        public String responseRate;
        public String avgRating;
        public List<Float> ratingTrend;

        public PerformanceStats(int totalViews, int totalBookings, int totalRevenue, String occupancyRate, String responseRate, String avgRating, List<Float> ratingTrend) {
            this.totalViews = totalViews;
            this.totalBookings = totalBookings;
            this.totalRevenue = totalRevenue;
            this.occupancyRate = occupancyRate;
            this.responseRate = responseRate;
            this.avgRating = avgRating;
            this.ratingTrend = ratingTrend;
        }
    }
}
