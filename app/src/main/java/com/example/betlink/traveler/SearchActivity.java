package com.example.betlink.traveler;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.betlink.R;
import com.example.betlink.auth.LoginActivity;
import com.example.betlink.databinding.ActivitySearchBinding;
import com.example.betlink.data.Listing;
import com.example.betlink.ui.ListingAdapter;
import com.example.betlink.ui.viewmodels.SearchViewModel;
import com.example.betlink.common.MessagesActivity;
import com.example.betlink.common.ProfileActivity;

public class SearchActivity extends AppCompatActivity {

    public static final String EXTRA_LISTING_ID = "extra_listing_id";

    private SearchViewModel viewModel;
    private ListingAdapter adapter;
    private String travelerName;
    private ActivitySearchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        travelerName = getIntent().getStringExtra(LoginActivity.EXTRA_TRAVELER_NAME);
        if (TextUtils.isEmpty(travelerName)) {
            travelerName = "Traveler";
        }

        binding.textTravelerGreeting.setText(getString(R.string.greeting_traveler, travelerName));

        adapter = new ListingAdapter(this::openListingDetails);
        binding.recyclerListings.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerListings.setAdapter(adapter);

        // Observe LiveData from ViewModel
        viewModel.getListings().observe(this, listings -> {
            adapter.submitList(listings);
            binding.textResultCount.setText(getString(R.string.result_count, listings.size()));
        });

        binding.buttonApplyFilters.setOnClickListener(v -> performSearch());
        
        setupNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        performSearch();
    }

    private void setupNavigation() {
        binding.bottomNavigation.setSelectedItemId(R.id.nav_explore);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_explore) {
                return true;
            } else if (itemId == R.id.nav_bookings) {
                Intent intent = new Intent(this, BookingStatusActivity.class);
                intent.putExtra(LoginActivity.EXTRA_TRAVELER_NAME, travelerName);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_messages) {
                startActivity(new Intent(this, MessagesActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra(LoginActivity.EXTRA_TRAVELER_NAME, travelerName);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void performSearch() {
        String query = binding.inputSearch.getText().toString().trim();
        String maxPriceText = binding.inputMaxPrice.getText().toString().trim();
        int maxPrice = 0;
        if (!maxPriceText.isEmpty()) {
            try {
                maxPrice = Integer.parseInt(maxPriceText);
            } catch (NumberFormatException ignored) {
                maxPrice = 0;
            }
        }

        boolean verifiedOnly = binding.checkboxVerifiedOnly.isChecked();
        viewModel.search(query, maxPrice, verifiedOnly);
    }

    private void openListingDetails(Listing listing) {
        Intent intent = new Intent(this, ListingDetailsActivity.class);
        intent.putExtra(EXTRA_LISTING_ID, listing.getId());
        intent.putExtra(LoginActivity.EXTRA_TRAVELER_NAME, travelerName);
        startActivity(intent);
    }
}
