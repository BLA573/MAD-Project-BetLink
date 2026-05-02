package com.example.betlink.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.betlink.R;
import com.example.betlink.data.Listing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ListingViewHolder> {

    public interface OnListingClickListener {
        void onListingClick(Listing listing);
    }

    private final List<Listing> items = new ArrayList<>();
    private final OnListingClickListener listener;

    public ListingAdapter(OnListingClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Listing> listings) {
        items.clear();
        items.addAll(listings);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_listing, parent, false);
        return new ListingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListingViewHolder holder, int position) {
        Listing listing = items.get(position);
        holder.title.setText(listing.getTitle());
        holder.subtitle.setText(String.format(Locale.US, "%s • %s", listing.getCity(), listing.getLandmark()));
        holder.description.setText(listing.getDescription());
        holder.priceBadge.setText(String.format(Locale.US, "%d ETB / night", listing.getPricePerNight()));
        
        holder.verifiedBadge.setVisibility(listing.isVerified() ? View.VISIBLE : View.GONE);
        
        if (listing.getImages() != null && !listing.getImages().isEmpty()) {
            String path = listing.getImages().get(0);
            Object source = (path.startsWith("/") || path.startsWith("content://")) ? new File(path) : path;
            if (path.startsWith("content://")) source = android.net.Uri.parse(path);
            else if (path.startsWith("/")) source = new File(path);

            Glide.with(holder.image.getContext())
                    .load(source)
                    .placeholder(R.drawable.header_traveler_bg)
                    .error(R.drawable.header_traveler_bg)
                    .centerCrop()
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.header_traveler_bg);
            int colorRes = (position % 2 == 0) ? R.color.traveler_tint : R.color.host_tint;
            holder.image.setBackgroundResource(colorRes);
        }

        holder.itemView.setOnClickListener(v -> listener.onListingClick(listing));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ListingViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final TextView title;
        final TextView subtitle;
        final TextView description;
        final TextView priceBadge;
        final View verifiedBadge;

        ListingViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageListing);
            title = itemView.findViewById(R.id.listingTitle);
            subtitle = itemView.findViewById(R.id.listingSubtitle);
            description = itemView.findViewById(R.id.listingDescription);
            priceBadge = itemView.findViewById(R.id.textPriceBadge);
            verifiedBadge = itemView.findViewById(R.id.listingVerification);
        }
    }
}
