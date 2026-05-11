package com.example.betlink.traveler;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.betlink.R;
import com.example.betlink.data.ListingRepository;
import com.example.betlink.data.Review;
import com.example.betlink.data.SessionManager;
import com.example.betlink.databinding.ActivityReviewsBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReviewsActivity extends AppCompatActivity {
    public static final String EXTRA_LISTING_ID = "extra_listing_id";

    private ActivityReviewsBinding binding;
    private final ListingRepository repository = ListingRepository.getInstance();
    private final ReviewsAdapter adapter = new ReviewsAdapter();
    private String listingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReviewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        listingId = getIntent().getStringExtra(EXTRA_LISTING_ID);
        if (listingId == null) {
            Toast.makeText(this, R.string.listing_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.recyclerReviews.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerReviews.setAdapter(adapter);

        binding.buttonSubmitReview.setOnClickListener(v -> submitReview());
        loadReviews();
    }

    private void loadReviews() {
        binding.textReviewState.setText("Loading reviews...");
        repository.getReviewsForListing(listingId, new ListingRepository.ListingCallback<List<Review>>() {
            @Override
            public void onSuccess(List<Review> result) {
                adapter.submitList(result);
                binding.textReviewState.setText(result.isEmpty()
                        ? "No reviews yet. Be the first to help future travelers."
                        : String.format(Locale.US, "%d traveler reviews", result.size()));
            }

            @Override
            public void onError(String message) {
                binding.textReviewState.setText("Reviews could not load.");
            }
        });
    }

    private void submitReview() {
        int rating = Math.max(1, Math.round(binding.ratingInput.getRating()));
        String comment = binding.inputReviewComment.getText() == null
                ? ""
                : binding.inputReviewComment.getText().toString().trim();
        if (comment.isEmpty()) {
            Toast.makeText(this, "Add a short review first.", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.buttonSubmitReview.setEnabled(false);
        Review review = new Review(listingId, SessionManager.getInstance().getUserId(), rating, comment);
        repository.createReview(review, new ListingRepository.ListingCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                binding.inputReviewComment.setText("");
                binding.buttonSubmitReview.setEnabled(true);
                Toast.makeText(ReviewsActivity.this, "Review submitted.", Toast.LENGTH_SHORT).show();
                loadReviews();
            }

            @Override
            public void onError(String message) {
                binding.buttonSubmitReview.setEnabled(true);
                Toast.makeText(ReviewsActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private static class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {
        private final List<Review> items = new ArrayList<>();

        void submitList(List<Review> reviews) {
            items.clear();
            items.addAll(reviews);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView view = (TextView) LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            view.setTextColor(parent.getContext().getResources().getColor(R.color.text_primary));
            view.setPadding(20, 18, 20, 18);
            return new ReviewViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
            Review review = items.get(position);
            holder.text.setText(String.format(Locale.US, "%d/5 - %s",
                    review.getRating(), review.getComment()));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ReviewViewHolder extends RecyclerView.ViewHolder {
            final TextView text;

            ReviewViewHolder(@NonNull View itemView) {
                super(itemView);
                text = (TextView) itemView;
            }
        }
    }
}
