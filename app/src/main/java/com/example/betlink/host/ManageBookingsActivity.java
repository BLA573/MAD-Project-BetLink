package com.example.betlink.host;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.betlink.data.BookingRequest;
import com.example.betlink.data.ListingRepository;
import com.example.betlink.databinding.ActivityManageBookingsBinding;
import com.example.betlink.ui.BookingRequestAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class ManageBookingsActivity extends AppCompatActivity {

    private ActivityManageBookingsBinding binding;
    private BookingRequestAdapter adapter;
    private final ListingRepository repository = ListingRepository.getInstance();
    private List<BookingRequest> allBookings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageBookingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.recyclerBookings.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new BookingRequestAdapter(new BookingRequestAdapter.OnBookingActionListener() {
            @Override
            public void onApprove(BookingRequest request) {
                updateStatus(request, BookingRequest.STATUS_APPROVED);
            }

            @Override
            public void onReject(BookingRequest request) {
                updateStatus(request, BookingRequest.STATUS_REJECTED);
            }

            @Override
            public void onItemClick(BookingRequest request) {
                // Future: show booking details
            }
        });
        
        binding.recyclerBookings.setAdapter(adapter);
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                applySelectedTab();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                refreshList();
            }
        });
        refreshList();
    }

    private void updateStatus(BookingRequest request, String newStatus) {
        repository.updateBookingStatus(request.getId(), newStatus,
                new ListingRepository.ListingCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        Toast.makeText(ManageBookingsActivity.this,
                                "Booking " + newStatus, Toast.LENGTH_SHORT).show();
                        refreshList();
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(ManageBookingsActivity.this,
                                "Error: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void refreshList() {
        repository.getBookings(new ListingRepository.ListingCallback<List<BookingRequest>>() {
            @Override
            public void onSuccess(List<BookingRequest> result) {
                allBookings = result;
                applySelectedTab();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ManageBookingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applySelectedTab() {
        int selected = binding.tabLayout.getSelectedTabPosition();
        List<BookingRequest> filtered = new ArrayList<>();
        for (BookingRequest booking : allBookings) {
            String status = booking.getStatus();
            if (selected == 0 && BookingRequest.STATUS_PENDING.equals(status)) {
                filtered.add(booking);
            } else if (selected == 1 && BookingRequest.STATUS_APPROVED.equals(status)) {
                filtered.add(booking);
            } else if (selected == 2 && !BookingRequest.STATUS_PENDING.equals(status)
                    && !BookingRequest.STATUS_APPROVED.equals(status)) {
                filtered.add(booking);
            }
        }
        adapter.submitList(filtered);
    }
}
