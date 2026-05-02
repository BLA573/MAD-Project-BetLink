package com.example.betlink.traveler;
import com.example.betlink.R;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.betlink.data.BookingRequest;
import com.example.betlink.data.MockRepository;
import com.example.betlink.databinding.ActivityBookingStatusBinding;

public class BookingStatusActivity extends AppCompatActivity {

    private final MockRepository repository = MockRepository.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityBookingStatusBinding binding = ActivityBookingStatusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int bookingId = getIntent().getIntExtra(ListingDetailsActivity.EXTRA_BOOKING_ID, -1);
        BookingRequest request = repository.getBookingById(bookingId);
        if (request == null) {
            Toast.makeText(this, R.string.booking_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindStatus(binding, request);

        binding.buttonRefreshStatus.setOnClickListener(v -> {
            BookingRequest refreshed = repository.getBookingById(bookingId);
            if (refreshed != null) {
                bindStatus(binding, refreshed);
            }
        });

        binding.buttonOpenHostDashboard.setOnClickListener(v ->
                startActivity(new Intent(this, HostDashboardActivity.class))
        );
    }

    private void bindStatus(ActivityBookingStatusBinding binding, BookingRequest request) {
        binding.textBookingIdValue.setText(getString(R.string.booking_id_value, request.getId()));
        binding.textTravelerValue.setText(request.getTravelerName());
        binding.textDatesValue.setText(getString(R.string.booking_dates, request.getCheckInDate(), request.getCheckOutDate()));
        binding.textStatusValue.setText(getString(R.string.booking_status_value, request.getStatus()));
    }
}

