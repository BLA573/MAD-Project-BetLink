package com.example.betlink.traveler;
import com.example.betlink.R;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.betlink.data.BookingRequest;
import com.example.betlink.data.ListingRepository;
import com.example.betlink.databinding.ActivityBookingStatusBinding;
import com.example.betlink.host.HostDashboardActivity;

import java.util.List;

public class BookingStatusActivity extends AppCompatActivity {

    private final ListingRepository repository = ListingRepository.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityBookingStatusBinding binding = ActivityBookingStatusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String bookingId = getIntent().getStringExtra(ListingDetailsActivity.EXTRA_BOOKING_ID);
        if (bookingId == null) {
            Toast.makeText(this, "Open a booking from your booking history.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        refreshStatus(binding, bookingId);

        binding.buttonRefreshStatus.setOnClickListener(v -> refreshStatus(binding, bookingId));

        binding.buttonOpenHostDashboard.setOnClickListener(v ->
                startActivity(new Intent(this, HostDashboardActivity.class))
        );
    }

    private void refreshStatus(ActivityBookingStatusBinding binding, String bookingId) {
        repository.getBookings(new ListingRepository.ListingCallback<List<BookingRequest>>() {
            @Override
            public void onSuccess(List<BookingRequest> result) {
                BookingRequest found = null;
                for (BookingRequest r : result) {
                    if (r.getId().equals(bookingId)) {
                        found = r;
                        break;
                    }
                }
                
                if (found != null) {
                    bindStatus(binding, found);
                } else {
                    Toast.makeText(BookingStatusActivity.this, R.string.booking_not_found, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(BookingStatusActivity.this, "Error fetching: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindStatus(ActivityBookingStatusBinding binding, BookingRequest request) {
        String id = request.getId();
        String shortId = id == null ? "Unknown" : (id.length() > 8 ? id.substring(0, 8) + "..." : id);
        binding.textBookingIdValue.setText("ID: " + shortId);
        binding.textTravelerValue.setText(request.getTravelerId());
        binding.textDatesValue.setText(getString(R.string.booking_dates, request.getCheckInDate(), request.getCheckOutDate()));
        binding.textStatusValue.setText(getString(R.string.booking_status_value, request.getStatus()));
    }
}
