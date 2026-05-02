package com.example.betlink.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.betlink.data.Listing;
import com.example.betlink.data.MockRepository;

import java.util.List;

public class SearchViewModel extends ViewModel {
    private final MockRepository repository = MockRepository.getInstance();
    private final MutableLiveData<List<Listing>> listings = new MutableLiveData<>();

    public LiveData<List<Listing>> getListings() {
        return listings;
    }

    public void search(String query, int maxPrice, boolean verifiedOnly) {
        List<Listing> result = repository.searchListings(query, maxPrice, verifiedOnly);
        listings.setValue(result);
    }
}
