package com.example.betlink.traveler;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.betlink.data.BookingRequest;
import com.example.betlink.data.ListingRepository;
import com.example.betlink.data.SessionManager;
import com.example.betlink.databinding.ActivityMyBookingsBinding;
import com.example.betlink.ui.BookingRequestAdapter;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

public class MyBookingsActivity extends AppCompatActivity {

    private ActivityMyBookingsBinding binding;
    private BookingRequestAdapter adapter;
    private final ListingRepository repository = ListingRepository.getInstance();
    private List<BookingRequest> allBookings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyBookingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.recyclerBookings.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BookingRequestAdapter(new BookingRequestAdapter.OnBookingActionListener() {
            @Override
            public void onApprove(BookingRequest request) {}

            @Override
            public void onReject(BookingRequest request) {}

            @Override
            public void onItemClick(BookingRequest request) {
                Intent intent = new Intent(MyBookingsActivity.this, BookingStatusActivity.class);
                intent.putExtra(ListingDetailsActivity.EXTRA_BOOKING_ID, request.getId());
                startActivity(intent);
            }
        }, false);
        binding.recyclerBookings.setAdapter(adapter);

        binding.tabLayoutMyBookings.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                refreshList();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        refreshList();
    }

    private void refreshList() {
        repository.getBookings(new ListingRepository.ListingCallback<List<BookingRequest>>() {
            @Override
            public void onSuccess(List<BookingRequest> result) {
                String currentUserId = SessionManager.getInstance().getUserId();
                allBookings = new ArrayList<>();
                for (BookingRequest booking : result) {
                    if (currentUserId == null || currentUserId.equals(booking.getTravelerId())) {
                        allBookings.add(booking);
                    }
                }
                applySelectedTab();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(MyBookingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                adapter.submitList(new ArrayList<>());
            }
        });
    }

    private void applySelectedTab() {
        int selected = binding.tabLayoutMyBookings.getSelectedTabPosition();
        List<BookingRequest> filtered = new ArrayList<>();
        for (BookingRequest booking : allBookings) {
            boolean rejected = BookingRequest.STATUS_REJECTED.equals(booking.getStatus());
            if ((selected == 0 && !rejected) || (selected == 1 && rejected)) {
                filtered.add(booking);
            }
        }
        adapter.submitList(filtered);
    }
}
