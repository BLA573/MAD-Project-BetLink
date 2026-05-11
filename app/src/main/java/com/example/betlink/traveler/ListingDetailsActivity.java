package com.example.betlink.traveler;

import com.example.betlink.R;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.betlink.data.BookingRequest;
import com.example.betlink.data.Listing;
import com.example.betlink.data.ListingRepository;
import com.example.betlink.databinding.ActivityListingDetailsBinding;
import com.example.betlink.auth.LoginActivity;

import java.io.File;
import java.util.List;

public class ListingDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_BOOKING_ID = "extra_booking_id";
    private final ListingRepository listingRepository = ListingRepository.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityListingDetailsBinding binding = ActivityListingDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String listingId = getIntent().getStringExtra(SearchActivity.EXTRA_LISTING_ID);
        String travelerName = getIntent().getStringExtra(LoginActivity.EXTRA_TRAVELER_NAME);

        // Fetch listing from live repository
        listingRepository.getListings(new ListingRepository.ListingCallback<List<Listing>>() {
            @Override
            public void onSuccess(List<Listing> result) {
                Listing listing = null;
                for (Listing l : result) {
                    if (l.getId().equals(listingId)) {
                        listing = l;
                        break;
                    }
                }
                
                if (listing != null) {
                    displayListing(listing, binding, travelerName, listingId);
                } else {
                    handleNotFound();
                }
            }

            @Override
            public void onError(String message) {
                handleNotFound();
            }
        });
    }

    private void displayListing(Listing listing, ActivityListingDetailsBinding binding, String travelerName, String listingId) {
        binding.textListingTitle.setText(listing.getTitle());
        binding.textLocationValue.setText(getString(R.string.listing_location_value, listing.getCity(), listing.getLandmark()));
        binding.textPriceValue.setText(getString(R.string.price_value, listing.getPricePerNight()));
        binding.textAmenitiesValue.setText(listing.getAmenities());
        binding.textDescriptionValue.setText(listing.getDescription());
        binding.textCancellationValue.setText(listing.getCancellationRule());
        binding.textVerifiedValue.setText(listing.isVerified() ? R.string.verified_badge : R.string.unverified_badge);
        binding.textRatingValue.setText(String.format(java.util.Locale.US, "★ %.1f", listing.getAvgRating()));

        if (listing.getImageUrls() != null && !listing.getImageUrls().isEmpty()) {
            String imagePath = listing.getImageUrls().get(0);
            Object imageSource = (imagePath.startsWith("/") || imagePath.startsWith("content://")) ? new File(imagePath) : imagePath;
            Glide.with(this)
                    .load(imageSource)
                    .placeholder(R.drawable.header_traveler_bg)
                    .error(R.drawable.header_traveler_bg)
                    .centerCrop()
                    .into(binding.imageDetailHero);
        }

        binding.buttonRequestBooking.setOnClickListener(v -> {
            String checkIn = binding.inputCheckIn.getText().toString().trim();
            String checkOut = binding.inputCheckOut.getText().toString().trim();
            if (TextUtils.isEmpty(checkIn) || TextUtils.isEmpty(checkOut)) {
                Toast.makeText(this, R.string.checkin_checkout_required, Toast.LENGTH_SHORT).show();
                return;
            }

            String travelerId = com.example.betlink.data.SessionManager.getInstance().getUserId();
            
            BookingRequest newRequest = new BookingRequest(
                    listingId,
                    travelerId,
                    checkIn,
                    checkOut,
                    BookingRequest.STATUS_PENDING
            );

            binding.buttonRequestBooking.setEnabled(false);
            binding.buttonRequestBooking.setText("Sending request...");
            listingRepository.createBookingAndReturn(newRequest, new ListingRepository.ListingCallback<BookingRequest>() {
                @Override
                public void onSuccess(BookingRequest result) {
                    Toast.makeText(ListingDetailsActivity.this, "Booking Requested!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ListingDetailsActivity.this, BookingStatusActivity.class);
                    intent.putExtra(EXTRA_BOOKING_ID, result.getId());
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(String message) {
                    binding.buttonRequestBooking.setEnabled(true);
                    binding.buttonRequestBooking.setText("Request to Book");
                    Toast.makeText(ListingDetailsActivity.this, "Booking Failed: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        });

        binding.buttonViewReviews.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReviewsActivity.class);
            intent.putExtra(ReviewsActivity.EXTRA_LISTING_ID, listingId);
            startActivity(intent);
        });
    }

    private void handleNotFound() {
        Toast.makeText(this, R.string.listing_not_found, Toast.LENGTH_SHORT).show();
        finish();
    }
}
