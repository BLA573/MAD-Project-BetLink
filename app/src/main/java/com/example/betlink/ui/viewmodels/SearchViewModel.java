package com.example.betlink.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.betlink.data.Listing;
import com.example.betlink.data.ListingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchViewModel extends ViewModel {
    private final ListingRepository repository = ListingRepository.getInstance();
    private final MutableLiveData<List<Listing>> listings = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public LiveData<List<Listing>> getListings() {
        return listings;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void search(String query, int maxPrice, boolean verifiedOnly) {
        isLoading.setValue(true);
        error.setValue(null);
        repository.getListings(new ListingRepository.ListingCallback<List<Listing>>() {
            @Override
            public void onSuccess(List<Listing> allListings) {
                isLoading.setValue(false);
                
                // Client-side filtering for search
                String normalizedQuery = query == null ? "" : query.trim().toLowerCase(Locale.US);
                List<Listing> result = new ArrayList<>();
                
                for (Listing l : allListings) {
                    boolean textMatch = normalizedQuery.isEmpty()
                            || (l.getCity() != null && l.getCity().toLowerCase(Locale.US).contains(normalizedQuery))
                            || (l.getLandmark() != null && l.getLandmark().toLowerCase(Locale.US).contains(normalizedQuery))
                            || (l.getTitle() != null && l.getTitle().toLowerCase(Locale.US).contains(normalizedQuery));
                    
                    boolean priceMatch = maxPrice <= 0 || l.getPricePerNight() <= maxPrice;
                    boolean verifiedMatch = !verifiedOnly || l.isVerified();
                    
                    if (textMatch && priceMatch && verifiedMatch) {
                        result.add(l);
                    }
                }
                listings.setValue(result);
            }

            @Override
            public void onError(String message) {
                isLoading.setValue(false);
                error.setValue(message);
                listings.setValue(new ArrayList<>());
            }
        });
    }
}
