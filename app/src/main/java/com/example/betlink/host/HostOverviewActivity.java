package com.example.betlink.host;
import com.example.betlink.R;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.betlink.data.MockRepository;
import com.example.betlink.databinding.ActivityHostOverviewBinding;
import com.example.betlink.ui.ListingAdapter;

public class HostOverviewActivity extends AppCompatActivity {

    private ActivityHostOverviewBinding binding;
    private ListingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHostOverviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = new ListingAdapter(listing -> {
            // Edit listing flow could go here
        });

        binding.recyclerHostListings.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerHostListings.setAdapter(adapter);

        binding.buttonAddListing.setOnClickListener(v -> {
            startActivity(new Intent(this, AddListingActivity.class));
        });

        binding.bottomNavigation.setSelectedItemId(R.id.nav_host_listings);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_host_dashboard) {
                finish();
                return true;
            } else if (itemId == R.id.nav_host_listings) {
                return true;
            } else if (itemId == R.id.nav_host_bookings) {
                startActivity(new Intent(this, ManageBookingsActivity.class));
                return true;
            } else if (itemId == R.id.nav_host_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
        
        refreshListings();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshListings();
    }

    private void refreshListings() {
        adapter.submitList(MockRepository.getInstance().getListings());
    }
}
