package com.example.betlink.host;
import com.example.betlink.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.betlink.data.Listing;
import com.example.betlink.data.MockRepository;
import com.example.betlink.databinding.ActivityListingPreviewBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class ListingPreviewActivity extends AppCompatActivity {

    private ActivityListingPreviewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListingPreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get data from intent
        String title = getIntent().getStringExtra("title");
        String city = getIntent().getStringExtra("city");
        String landmark = getIntent().getStringExtra("landmark");
        String price = getIntent().getStringExtra("price");
        String description = getIntent().getStringExtra("description");
        String propertyType = getIntent().getStringExtra("propertyType");
        String roomType = getIntent().getStringExtra("roomType");
        String imagePath = getIntent().getStringExtra("imageUri");

        // Bind to preview card
        binding.itemListing.listingTitle.setText(title);
        binding.itemListing.listingSubtitle.setText(city + " • " + landmark);
        binding.itemListing.listingDescription.setText(description);
        binding.itemListing.textPriceBadge.setText(price + " ETB / night");
        
        if (imagePath != null) {
            Object source = imagePath.startsWith("/") ? new File(imagePath) : imagePath;
            Glide.with(this)
                    .load(source)
                    .placeholder(R.drawable.header_traveler_bg)
                    .error(R.drawable.header_traveler_bg)
                    .centerCrop()
                    .into(binding.itemListing.imageListing);
        }

        binding.buttonPublish.setOnClickListener(v -> {
            // Save to repository
            MockRepository repo = MockRepository.getInstance();
            Listing newListing = new Listing(
                    String.valueOf(repo.getNextListingId()),
                    "mock-host-id",
                    title,
                    city != null ? city : "Unknown",
                    landmark != null ? landmark : "Unknown",
                    Integer.parseInt(price),
                    false, // Unverified by default
                    "Standard Amenities",
                    description,
                    "Flexible cancellation",
                    0.0f,
                    roomType,
                    imagePath != null ? Collections.singletonList(imagePath) : new ArrayList<>()
            );
            repo.addListing(newListing);

            Toast.makeText(this, R.string.publish_success, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, HostDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        binding.buttonEdit.setOnClickListener(v -> finish());
    }
}
