package com.example.betlink.traveler;
import com.example.betlink.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.betlink.data.BookingRequest;
import com.example.betlink.data.Listing;
import com.example.betlink.data.MockRepository;
import com.example.betlink.databinding.ActivityListingDetailsBinding;
import com.example.betlink.auth.LoginActivity;

import java.io.File;

public class ListingDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_BOOKING_ID = "extra_booking_id";

    private final MockRepository repository = MockRepository.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityListingDetailsBinding binding = ActivityListingDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int listingId = getIntent().getIntExtra(SearchActivity.EXTRA_LISTING_ID, -1);
        String travelerName = getIntent().getStringExtra(LoginActivity.EXTRA_TRAVELER_NAME);

        Listing listing = repository.getListingById(listingId);
        if (listing == null) {
            Toast.makeText(this, R.string.listing_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.textListingTitle.setText(listing.getTitle());
        binding.textLocationValue.setText(getString(R.string.listing_location_value, listing.getCity(), listing.getLandmark()));
        binding.textPriceValue.setText(getString(R.string.price_value, listing.getPricePerNight()));
        binding.textAmenitiesValue.setText(listing.getAmenities());
        binding.textDescriptionValue.setText(listing.getDescription());
        binding.textCancellationValue.setText(listing.getCancellationRule());
        binding.textVerifiedValue.setText(listing.isVerified() ? R.string.verified_badge : R.string.unverified_badge);

        if (listing.getImages() != null && !listing.getImages().isEmpty()) {
            String imagePath = listing.getImages().get(0);
            Object imageSource = imagePath.startsWith("/") ? new File(imagePath) : imagePath;
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

            String safeTravelerName = TextUtils.isEmpty(travelerName) ? "Traveler" : travelerName;
            BookingRequest request = repository.createBookingRequest(
                    listingId,
                    safeTravelerName,
                    checkIn,
                    checkOut
            );
            Intent intent = new Intent(this, BookingStatusActivity.class);
            intent.putExtra(EXTRA_BOOKING_ID, request.getId());
            startActivity(intent);
        });
    }
}
