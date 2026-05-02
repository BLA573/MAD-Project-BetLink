package com.example.betlink.traveler;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.betlink.R;
import com.example.betlink.data.MockRepository;
import com.example.betlink.databinding.ActivityMyBookingsBinding;
import com.example.betlink.ui.BookingRequestAdapter;
import com.google.android.material.tabs.TabLayout;

public class MyBookingsActivity extends AppCompatActivity {

    private ActivityMyBookingsBinding binding;
    private BookingRequestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyBookingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.recyclerBookings.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BookingRequestAdapter(null); // No actions for traveler here
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
        adapter.submitList(MockRepository.getInstance().getBookingRequests());
    }
}
