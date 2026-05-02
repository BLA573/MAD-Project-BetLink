package com.example.betlink.host;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.betlink.data.BookingRequest;
import com.example.betlink.data.MockRepository;
import com.example.betlink.databinding.ActivityManageBookingsBinding;
import com.example.betlink.ui.BookingRequestAdapter;

public class ManageBookingsActivity extends AppCompatActivity {

    private ActivityManageBookingsBinding binding;
    private BookingRequestAdapter adapter;
    private final MockRepository repository = MockRepository.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageBookingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.recyclerBookings.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new BookingRequestAdapter(new BookingRequestAdapter.OnBookingActionListener() {
            @Override
            public void onApprove(BookingRequest request) {
                repository.updateBookingStatus(request.getId(), BookingRequest.STATUS_APPROVED);
                refreshList();
                Toast.makeText(ManageBookingsActivity.this, "Booking Approved", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReject(BookingRequest request) {
                repository.updateBookingStatus(request.getId(), BookingRequest.STATUS_REJECTED);
                refreshList();
                Toast.makeText(ManageBookingsActivity.this, "Booking Rejected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemClick(BookingRequest request) {
                // Future: show booking details
            }
        });
        
        binding.recyclerBookings.setAdapter(adapter);
        refreshList();
    }

    private void refreshList() {
        adapter.submitList(repository.getBookingRequests());
    }
}
