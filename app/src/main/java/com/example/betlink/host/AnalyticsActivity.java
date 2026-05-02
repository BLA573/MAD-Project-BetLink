package com.example.betlink.host;
import com.example.betlink.R;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.betlink.data.MockRepository;
import com.example.betlink.databinding.ActivityAnalyticsBinding;
import java.util.Locale;

public class AnalyticsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAnalyticsBinding binding = ActivityAnalyticsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MockRepository.PerformanceStats stats = MockRepository.getInstance().getPerformanceStats();

        binding.textRevenueValue.setText(String.format(Locale.US, "%,d ETB", stats.totalRevenue));
        binding.textViewsValue.setText(String.format(Locale.US, "%,d", stats.totalViews));
        binding.textRatingValue.setText(getString(R.string.listing_subtitle, "", "", 0).substring(0, 0) + stats.avgRating + " ★"); // Using a hack to avoid concatenating raw strings if possible, or just use string res
        
        // Proper way with string resources
        binding.textRevenueValue.setText(getString(R.string.price_value, stats.totalRevenue));
        binding.textViewsValue.setText(String.valueOf(stats.totalViews));
        binding.textTotalBookingsValue.setText(String.valueOf(stats.totalBookings));
        binding.textOccupancyRateValue.setText(stats.occupancyRate);
        binding.textResponseRateValue.setText(stats.responseRate);
        
        binding.textGrowthValue.setText("+12% from last month");
    }
}
