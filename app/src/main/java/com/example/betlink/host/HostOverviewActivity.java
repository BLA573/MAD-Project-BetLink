package com.example.betlink.host;

import com.example.betlink.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.betlink.common.ProfileActivity;
import com.example.betlink.databinding.ActivityHostOverviewBinding;
import com.example.betlink.ui.ListingAdapter;
import com.example.betlink.ui.viewmodels.HostDashboardViewModel;

public class HostOverviewActivity extends AppCompatActivity {

    private ActivityHostOverviewBinding binding;
    private ListingAdapter adapter;
    private HostDashboardViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHostOverviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(HostDashboardViewModel.class);

        adapter = new ListingAdapter(listing -> {
            // Edit listing flow could go here
        });

        binding.recyclerHostListings.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerHostListings.setAdapter(adapter);

        binding.buttonAddListing.setOnClickListener(v -> {
            startActivity(new Intent(this, AddListingActivity.class));
        });

        setupNavigation();
        observeViewModel();
        
        viewModel.loadStats();
    }

    private void observeViewModel() {
        viewModel.getHostListings().observe(this, listings -> {
            adapter.submitList(listings);
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            // Future: Show a refresh indicator or spinner here
        });
    }

    private void setupNavigation() {
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadStats();
    }
}
