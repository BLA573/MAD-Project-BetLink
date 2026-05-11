package com.example.betlink.data;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListingRepository {
    private static final String TAG = "ListingRepository";
    private static ListingRepository instance;
    private final SupabaseService service;
    private final String apiKey;

    private ListingRepository() {
        this.service = SupabaseClient.getService();
        this.apiKey = SupabaseClient.getAnonKey();
    }

    public static synchronized ListingRepository getInstance() {
        if (instance == null) {
            instance = new ListingRepository();
        }
        return instance;
    }

    public interface ListingCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    /**
     * Fetch all listings from Supabase.
     */
    public void getListings(ListingCallback<List<Listing>> callback) {
        service.getListings(apiKey, "*").enqueue(new Callback<List<Listing>>() {
            @Override
            public void onResponse(Call<List<Listing>> call, Response<List<Listing>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch listings: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Listing>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Create a new listing in Supabase.
     */
    public void createListing(Listing listing, ListingCallback<Void> callback) {
        service.createListing(apiKey, "return=minimal", listing).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        errorBody = "Could not parse error body";
                    }
                    Log.e(TAG, "Listing creation failed. Code: " + response.code() + " Error: " + errorBody);
                    callback.onError("Failed to create listing (" + response.code() + "): " + errorBody);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Upload an image to Supabase Storage and return the public URL.
     */
    public void uploadImage(Context context, Uri imageUri, String fileName, ListingCallback<String> callback) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            if (inputStream == null) {
                callback.onError("Could not open image stream");
                return;
            }

            byte[] bytes = getBytes(inputStream);
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), bytes);
            String token = SessionManager.getInstance().getAccessToken();
            String authHeader = "Bearer " + (token != null ? token : apiKey);

            // Upload to 'listing-images' bucket
            service.uploadFile(apiKey, authHeader, "image/jpeg", "listing-images", fileName, requestBody).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        String baseUrl = SupabaseClient.getBaseUrl();
                        if (!baseUrl.endsWith("/")) baseUrl += "/";
                        String publicUrl = baseUrl + "storage/v1/object/public/listing-images/" + fileName;
                        callback.onSuccess(publicUrl);
                    } else {
                        String errorBody = "";
                        try {
                            if (response.errorBody() != null) {
                                errorBody = response.errorBody().string();
                            }
                        } catch (Exception e) {
                            errorBody = "Could not parse error body";
                        }
                        Log.e(TAG, "Upload failed. Code: " + response.code() + " Error: " + errorBody);
                        callback.onError("Upload failed (" + response.code() + "): " + errorBody);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    callback.onError("Upload error: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            callback.onError("Error preparing upload: " + e.getMessage());
        }
    }

    /**
     * Fetch all bookings from Supabase.
     */
    public void getBookings(ListingCallback<List<BookingRequest>> callback) {
        service.getBookings(apiKey, "*").enqueue(new Callback<List<BookingRequest>>() {
            @Override
            public void onResponse(Call<List<BookingRequest>> call, Response<List<BookingRequest>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch bookings: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<BookingRequest>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Create a new booking in Supabase.
     */
    public void createBooking(BookingRequest booking, ListingCallback<Void> callback) {
        service.createBooking(apiKey, "return=minimal", booking).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        errorBody = "Unknown error";
                    }
                    callback.onError("Failed (" + response.code() + "): " + errorBody);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void createBookingAndReturn(BookingRequest booking, ListingCallback<BookingRequest> callback) {
        service.createBookingReturning(apiKey, "return=representation", booking)
                .enqueue(new Callback<List<BookingRequest>>() {
                    @Override
                    public void onResponse(Call<List<BookingRequest>> call,
                                           Response<List<BookingRequest>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && !response.body().isEmpty()) {
                            callback.onSuccess(response.body().get(0));
                        } else {
                            String errorBody = "";
                            try {
                                if (response.errorBody() != null) {
                                    errorBody = response.errorBody().string();
                                }
                            } catch (Exception e) {
                                errorBody = "Unknown error";
                            }
                            callback.onError("Failed (" + response.code() + "): " + errorBody);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<BookingRequest>> call, Throwable t) {
                        callback.onError("Network error: " + t.getMessage());
                    }
                });
    }

    /**
     * Approve or reject a booking.
     *
     * @param bookingId the UUID of the booking row
     * @param newStatus BookingRequest.STATUS_APPROVED or STATUS_REJECTED
     */
    public void updateBookingStatus(String bookingId, String newStatus,
                                    ListingCallback<Void> callback) {
        Map<String, String> body = new HashMap<>();
        body.put("status", newStatus);

        service.updateBookingStatus(apiKey, "eq." + bookingId, "return=minimal", body)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            callback.onSuccess(null);
                        } else {
                            String errorBody = "";
                            try {
                                if (response.errorBody() != null) {
                                    errorBody = response.errorBody().string();
                                }
                            } catch (Exception e) {
                                errorBody = "Unknown error";
                            }
                            callback.onError("Update failed (" + response.code() + "): " + errorBody);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        callback.onError("Network error: " + t.getMessage());
                    }
                });
    }

    public void getReviewsForListing(String listingId, ListingCallback<List<Review>> callback) {
        service.getReviews(apiKey, "*", "eq." + listingId).enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch reviews: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void createReview(Review review, ListingCallback<Void> callback) {
        service.createReview(apiKey, "return=minimal", review).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        errorBody = "Unknown error";
                    }
                    callback.onError("Review failed (" + response.code() + "): " + errorBody);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    private byte[] getBytes(InputStream inputStream) throws Exception {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}
