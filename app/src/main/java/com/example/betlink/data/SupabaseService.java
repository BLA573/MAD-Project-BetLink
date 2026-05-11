package com.example.betlink.data;

import java.util.List;
import java.util.Map;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SupabaseService {

    // --- Authentication ---

    /** Supabase Auth – Sign up with email + password */
    @POST("auth/v1/signup")
    Call<AuthResponse> signUp(
        @Header("apikey") String apiKey,
        @Body AuthRequest request
    );

    /** Supabase Auth – Login with email + password */
    @POST("auth/v1/token?grant_type=password")
    Call<AuthResponse> login(
        @Header("apikey") String apiKey,
        @Body AuthRequest request
    );

    /** Supabase Auth – Send password recovery email */
    @POST("auth/v1/recover")
    Call<Void> recoverPassword(
        @Header("apikey") String apiKey,
        @Body Map<String, String> body
    );

    // --- Listings (PostgREST) ---

    /** Fetch all listings */
    @GET("rest/v1/listings")
    Call<List<Listing>> getListings(
        @Header("apikey") String apiKey,
        @Query("select") String select
    );

    /** Create a new listing */
    @POST("rest/v1/listings")
    Call<Void> createListing(
        @Header("apikey") String apiKey,
        @Header("Prefer") String prefer, // Use "return=minimal" or "return=representation"
        @Body Listing listing
    );

    // --- Storage ---

    /** Upload a file to Supabase Storage */
    @POST("storage/v1/object/{bucket}/{path}")
    Call<ResponseBody> uploadFile(
        @Header("apikey") String apiKey,
        @Header("Authorization") String authorization,
        @Header("Content-Type") String contentType,
        @Path("bucket") String bucket,
        @Path("path") String path,
        @Body RequestBody file
    );

    // --- Bookings ---

    /** Fetch all bookings (filtered by RLS) */
    @GET("rest/v1/bookings")
    Call<List<BookingRequest>> getBookings(
        @Header("apikey") String apiKey,
        @Query("select") String select
    );

    /** Create a new booking request */
    @POST("rest/v1/bookings")
    Call<Void> createBooking(
        @Header("apikey") String apiKey,
        @Header("Prefer") String prefer,
        @Body BookingRequest booking
    );

    /** Create a booking and return the inserted row. */
    @POST("rest/v1/bookings")
    Call<List<BookingRequest>> createBookingReturning(
        @Header("apikey") String apiKey,
        @Header("Prefer") String prefer,
        @Body BookingRequest booking
    );

    /**
     * Update a single booking's status.
     * PostgREST filter: ?id=eq.<bookingId>
     */
    @PATCH("rest/v1/bookings")
    Call<Void> updateBookingStatus(
        @Header("apikey") String apiKey,
        @Query("id") String idFilter,
        @Header("Prefer") String prefer,
        @Body Map<String, String> body
    );

    // --- Reviews ---

    @GET("rest/v1/reviews")
    Call<List<Review>> getReviews(
        @Header("apikey") String apiKey,
        @Query("select") String select,
        @Query("listing_id") String listingFilter
    );

    @POST("rest/v1/reviews")
    Call<Void> createReview(
        @Header("apikey") String apiKey,
        @Header("Prefer") String prefer,
        @Body Review review
    );
}
