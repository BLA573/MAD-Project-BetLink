package com.example.betlink.host;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.betlink.R;
import com.example.betlink.databinding.ActivityHostDashboardBinding;
import com.example.betlink.ui.viewmodels.HostDashboardViewModel;
import com.example.betlink.common.MessagesActivity;
import com.example.betlink.common.ProfileActivity;

public class HostDashboardActivity extends AppCompatActivity {

    private HostDashboardViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityHostDashboardBinding binding = ActivityHostDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(HostDashboardViewModel.class);

        // Navigation to various host functions
        binding.buttonAddListing.setOnClickListener(v -> 
            startActivity(new Intent(this, AddListingActivity.class)));
            
        binding.buttonViewRequests.setOnClickListener(v -> 
            startActivity(new Intent(this, ManageBookingsActivity.class)));
            
        binding.buttonManageAvailability.setOnClickListener(v -> 
            startActivity(new Intent(this, AvailabilityCalendarActivity.class)));
            
        binding.buttonMessages.setOnClickListener(v -> 
            startActivity(new Intent(this, MessagesActivity.class)));
            
        binding.buttonPerformance.setOnClickListener(v -> 
            startActivity(new Intent(this, AnalyticsActivity.class)));
            
        binding.buttonProfile.setOnClickListener(v -> 
            startActivity(new Intent(this, ProfileActivity.class)));

        // Observe ViewModel stats
        viewModel.getListingsCount().observe(this, count -> binding.textListingsCount.setText(count));
        viewModel.getPendingCount().observe(this, count -> binding.textPendingCount.setText(count));
        viewModel.getApprovedCount().observe(this, count -> binding.textApprovedCount.setText(count));
        viewModel.getOccupancyRate().observe(this, rate -> binding.textOccupancyRate.setText(rate));

        setupNavigation(binding);
        viewModel.loadStats();
    }

    private void setupNavigation(ActivityHostDashboardBinding binding) {
        binding.bottomNavigation.setSelectedItemId(R.id.nav_host_dashboard);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_host_dashboard) {
                return true;
            } else if (itemId == R.id.nav_host_listings) {
                startActivity(new Intent(this, HostOverviewActivity.class));
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
}
