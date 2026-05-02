package com.example.betlink.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.betlink.data.BookingRequest;
import com.example.betlink.data.Listing;
import com.example.betlink.data.MockRepository;

import java.util.List;

public class HostDashboardViewModel extends ViewModel {
    private final MockRepository repository = MockRepository.getInstance();
    
    private final MutableLiveData<String> listingsCount = new MutableLiveData<>();
    private final MutableLiveData<String> pendingCount = new MutableLiveData<>();
    private final MutableLiveData<String> approvedCount = new MutableLiveData<>();
    private final MutableLiveData<String> occupancyRate = new MutableLiveData<>();

    public LiveData<String> getListingsCount() { return listingsCount; }
    public LiveData<String> getPendingCount() { return pendingCount; }
    public LiveData<String> getApprovedCount() { return approvedCount; }
    public LiveData<String> getOccupancyRate() { return occupancyRate; }

    public void loadStats() {
        List<Listing> listings = repository.getListings();
        List<BookingRequest> requests = repository.getBookingRequests();

        int pending = 0;
        int approved = 0;
        for (BookingRequest r : requests) {
            if (BookingRequest.STATUS_PENDING.equals(r.getStatus())) pending++;
            else if (BookingRequest.STATUS_APPROVED.equals(r.getStatus())) approved++;
        }

        listingsCount.setValue(String.valueOf(listings.size()));
        pendingCount.setValue(String.valueOf(pending));
        approvedCount.setValue(String.valueOf(approved));
        
        // Mock occupancy logic
        int rate = listings.isEmpty() ? 0 : (approved * 100 / (listings.size() * 5 + 1));
        if (rate > 100) rate = 95;
        occupancyRate.setValue(rate + "%");
    }
}
