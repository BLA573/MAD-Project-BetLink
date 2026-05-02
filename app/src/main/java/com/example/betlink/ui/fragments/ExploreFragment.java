package com.example.betlink.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.betlink.traveler.ListingDetailsActivity;
import com.example.betlink.auth.LoginActivity;
import com.example.betlink.R;
import com.example.betlink.traveler.SearchActivity;
import com.example.betlink.data.Listing;
import com.example.betlink.databinding.FragmentExploreBinding;
import com.example.betlink.ui.ListingAdapter;
import com.example.betlink.ui.viewmodels.SearchViewModel;

public class ExploreFragment extends Fragment {

    private FragmentExploreBinding binding;
    private SearchViewModel viewModel;
    private ListingAdapter adapter;
    private String travelerName;

    public static ExploreFragment newInstance(String travelerName) {
        ExploreFragment fragment = new ExploreFragment();
        Bundle args = new Bundle();
        args.putString(LoginActivity.EXTRA_TRAVELER_NAME, travelerName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            travelerName = getArguments().getString(LoginActivity.EXTRA_TRAVELER_NAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentExploreBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);

        binding.textTravelerGreeting.setText(getString(R.string.greeting_traveler, travelerName));

        adapter = new ListingAdapter(this::openListingDetails);
        binding.recyclerListings.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerListings.setAdapter(adapter);

        viewModel.getListings().observe(getViewLifecycleOwner(), listings -> {
            adapter.submitList(listings);
            binding.textResultCount.setText(getString(R.string.result_count, listings.size()));
        });

        binding.buttonApplyFilters.setOnClickListener(v -> performSearch());
        
        performSearch();
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
        Intent intent = new Intent(requireContext(), ListingDetailsActivity.class);
        intent.putExtra(SearchActivity.EXTRA_LISTING_ID, listing.getId());
        intent.putExtra(LoginActivity.EXTRA_TRAVELER_NAME, travelerName);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
