package com.example.betlink.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.betlink.data.Listing;
import com.example.betlink.data.BookingRequest;
import com.example.betlink.data.ListingRepository;
import com.example.betlink.data.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class HostDashboardViewModel extends ViewModel {
    private final ListingRepository repository = ListingRepository.getInstance();
    
    private final MutableLiveData<String> listingsCount = new MutableLiveData<>("0");
    private final MutableLiveData<String> pendingCount = new MutableLiveData<>("0");
    private final MutableLiveData<String> approvedCount = new MutableLiveData<>("0");
    private final MutableLiveData<String> occupancyRate = new MutableLiveData<>("0%");
    
    private final MutableLiveData<List<Listing>> hostListings = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public LiveData<String> getListingsCount() { return listingsCount; }
    public LiveData<String> getPendingCount() { return pendingCount; }
    public LiveData<String> getApprovedCount() { return approvedCount; }
    public LiveData<String> getOccupancyRate() { return occupancyRate; }
    public LiveData<List<Listing>> getHostListings() { return hostListings; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void loadStats() {
        isLoading.setValue(true);
        String currentHostId = SessionManager.getInstance().getUserId();
        
        repository.getListings(new ListingRepository.ListingCallback<List<Listing>>() {
            @Override
            public void onSuccess(List<Listing> allListings) {
                isLoading.setValue(false);
                
                // Filter listings for this specific host
                List<Listing> filteredListings = new ArrayList<>();
                for (Listing l : allListings) {
                    if (currentHostId != null && currentHostId.equals(l.getHostId())) {
                        filteredListings.add(l);
                    }
                }
                
                hostListings.setValue(filteredListings);
                listingsCount.setValue(String.valueOf(filteredListings.size()));
                loadBookingStats(filteredListings);
            }

            @Override
            public void onError(String message) {
                isLoading.setValue(false);
                // Handle error (e.g. log or show message)
            }
        });
    }

    private void loadBookingStats(List<Listing> filteredListings) {
        repository.getBookings(new ListingRepository.ListingCallback<List<BookingRequest>>() {
            @Override
            public void onSuccess(List<BookingRequest> bookings) {
                int pending = 0;
                int approved = 0;
                for (BookingRequest booking : bookings) {
                    boolean belongsToHost = false;
                    for (Listing listing : filteredListings) {
                        if (listing.getId() != null && listing.getId().equals(booking.getListingId())) {
                            belongsToHost = true;
                            break;
                        }
                    }
                    if (!belongsToHost) {
                        continue;
                    }
                    if (BookingRequest.STATUS_PENDING.equals(booking.getStatus())) {
                        pending++;
                    } else if (BookingRequest.STATUS_APPROVED.equals(booking.getStatus())) {
                        approved++;
                    }
                }
                pendingCount.setValue(String.valueOf(pending));
                approvedCount.setValue(String.valueOf(approved));
                int total = pending + approved;
                occupancyRate.setValue(total == 0 ? "0%" : Math.min(100, approved * 100 / total) + "%");
            }

            @Override
            public void onError(String message) {
                pendingCount.setValue("0");
                approvedCount.setValue("0");
                occupancyRate.setValue("0%");
            }
        });
    }
}
